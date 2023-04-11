package cn.aishu.exporter.common.output;

import cn.aishu.exporter.common.utils.GzipCompressUtil;
import cn.aishu.exporter.common.utils.TimeUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HttpSender implements Sender {
    private static final int THREAD_NUM = 5;
    private static final int STR_LIST_SIZE_LIMIT = 80;
    private static final int STR_LENGTH_LIMIT = 5 * 1024 * 1000;  // 5MB
    private static final int HTTP_TIMEOUT_MILLISECONDS = 15000;

    private ExecutorService threadPool = null;
    private int capacity = 65535;
    private final BlockingQueue<Serializer> queue = new LinkedBlockingQueue<>(capacity);
    private String serverUrl;
    private boolean isShutDown = false;
    private Retry retry = new Retry();
    private boolean isGzip = false;
    private final Log logger = LogFactory.getLog(getClass());


    public static Sender create(String url){
        if(url.startsWith("https")){
            return new HttpsSender(url);
        }
        return new HttpSender(url);}

    public static Sender create(String url, Retry retry, boolean isGzip, int cacheCapacity){
        if(url.startsWith("https")){
            return new HttpsSender(url, retry, isGzip, cacheCapacity);
        }
        return new HttpSender(url, retry, isGzip, cacheCapacity);
    }

    public HttpSender(String url) {
        this.serverUrl = url;
        //启动发送线程
        serviceStart();
    }

    public HttpSender(String url, Retry retry, boolean isGzip, int cacheCapacity) {
        this.serverUrl = url;
        this.isGzip = isGzip;
        if (retry != null) {
            this.retry = retry;
        }
        this.capacity = cacheCapacity;
        //启动发送线程
        serviceStart();
    }

    @Override
    public void send(Serializer logContent) {
        if (isShutDown) {
            return;
        }

        try {
            // 超过队列容量直接丢弃日志
            if (queue.size() < capacity) {
                queue.put(logContent);
            }else{
                this.logger.warn("缓冲弃区满，将丢新进数据" + logContent.toJson());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.error(e);
        }
    }

    @Override
    public void shutDown() {
        isShutDown = true;
        threadPool.shutdown();
    }

    public void httpRequest(String outputStr, int retryInterval, int retryElapsedTime) {
        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        HttpURLConnection conn = null;
        try {
            URL creator = new URL(this.serverUrl);
            conn = (HttpURLConnection) creator.openConnection();
            if(conn == null){
                this.logger.info("http create connection error");
                httpRequest(outputStr, retryInterval, retryElapsedTime);
                return;
            }
            if (this.isGzip){
                conn.setRequestProperty("Content-Encoding", "gzip");
            } else {
                conn.setRequestProperty("Content-Type", "Application/json");
            }
            conn.setConnectTimeout(HTTP_TIMEOUT_MILLISECONDS);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            // 往服务器端写内容
            OutputStream outputStream = conn.getOutputStream();
            if (isGzip) {
                outputStream.write(GzipCompressUtil.compressData(outputStr, StandardCharsets.UTF_8.toString()));
            } else {
                outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                return;
            } else {
                this.logger.error(conn.getResponseMessage());
            }
            // 当网络不稳定时(TooManyRequests:429, InternalServerError:500,
            // ServiceUnavailable:503)，触发重发机制
            if (Retry.isOK(retry, retryElapsedTime, responseCode) && (queue.size() < capacity)) {
                int currentRetryInterval = retryInterval + retry.getInitialInterval();
                if (currentRetryInterval > retry.getMaxInterval()) {
                    currentRetryInterval = retry.getMaxInterval();
                }
                int currentRetryElapsedTime = retryElapsedTime + currentRetryInterval;
                TimeUtil.sleepSecond(currentRetryInterval);

                httpRequest(outputStr, currentRetryInterval, currentRetryElapsedTime);
            }
            this.logger.error("error: 发送http目的地址:" + this.serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            this.logger.error("http send error:" , e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    /**
     * 启动事件循环
     */
    public synchronized void serviceStart() {
        // 创建一个线程池的线程池
        if (threadPool == null || threadPool.isTerminated()) {
            threadPool = Executors.newFixedThreadPool(THREAD_NUM);
            for (int i = 0; i < THREAD_NUM; i++) {
                threadPool.submit(createThread());
            }
        }
    }

    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            int strLength = 0;
            List<String> list = new ArrayList<>(STR_LIST_SIZE_LIMIT);
            boolean getNull;
            boolean loop = true;
            while (loop) {
                try {
                    Serializer content = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (content != null) {
                        getNull = false;
                        String contentStr = content.toJson();
                        strLength += contentStr.length();
                        if (strLength >= STR_LENGTH_LIMIT) {
                            // 如果字符总长度超过了限制，先发送这批trace
                            sendAndClearList(list);
                            strLength = 0;
                            list.add(contentStr);
                            continue;
                        } else {
                            list.add(contentStr);
                        }
                    } else {
                        getNull = true;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (list.size() == STR_LIST_SIZE_LIMIT) {
                    // 如果trace的条数超过了预设值，先发送这批trace
                    sendAndClearList(list);
                    strLength = 0;
                }
                if (getNull) {
                    // 如果此次循环没有获取到数据，把之前缓存的数据先发送出去，防止数据滞留不发
                    if (!list.isEmpty()) {
                        sendAndClearList(list);
                        strLength = 0;
                    }
                    if (this.isShutDown && this.queue.isEmpty()) {
                        loop = false;
                    }
                }
            }
        };
    }

    private void sendAndClearList(List<String> list) {
        httpRequest("[" + String.join(",", list) + "]", 0, 0);
        list.clear();
    }
}
