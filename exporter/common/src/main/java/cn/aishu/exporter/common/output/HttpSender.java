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

    private ExecutorService threadPool = null;
    private  int CAPACITY = 4096;
    private final BlockingQueue<Serializer> queue = new LinkedBlockingQueue<>(CAPACITY);
    private static final int LIST_SIZE = 80;
    private final String url;
    private boolean isShutDown = false;
    private Retry retry = new Retry();
    private boolean isGzip = true;
    private int threadNum = 5;

    // 5MB
    private final int strLengthLimit = 5 * 1024 * 1000;
    public final Log LOGGER =  LogFactory.getLog(getClass());


    public static HttpSender create(String url, Retry retry, boolean isGzip, int cacheCapacity){
        return new HttpSender(url, retry, isGzip, cacheCapacity);
    }

    public HttpSender(String url, Retry retry, boolean isGzip, int cacheCapacity) {
        this.url = url;
        this.isGzip = isGzip;
        if(retry != null){
            this.retry = retry;
        }

        this.CAPACITY = cacheCapacity;
        //启动发送线程
        serviceStart();
    }


    @Override
    public void send(Serializer logContent) {
        if (isShutDown) {
            return;
        }

        try {
            //超过队列容量直接丢弃日志
            if (queue.size() < CAPACITY) {
                queue.put(logContent);
            }else{
                this.LOGGER.warn("缓冲区满，将丢弃新进数据");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.LOGGER.error(e);
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
            URL url = new URL(this.url);
            conn = (HttpURLConnection) url.openConnection();
            if (isGzip){
                conn.setRequestProperty("Content-Encoding", "gzip");
            }else {
                conn.setRequestProperty("Content-Type", "Application/json");
            }
            conn.setConnectTimeout(15000);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            //往服务器端写内容
            OutputStream outputStream = conn.getOutputStream();
            if (isGzip) {
                outputStream.write(GzipCompressUtil.compressData(outputStr, StandardCharsets.UTF_8.toString()));
            } else {
                outputStream.write(outputStr.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 204 || responseCode == 200) {
                return;
            }
            //当网络不稳定时(TooManyRequests:429, InternalServerError:500, ServiceUnavailable:503)，触发重发机制
            if (Retry.isOK(retry, retryElapsedTime, responseCode) && (queue.size() < CAPACITY)) {
                int currentRetryInterval = retryInterval + retry.getInitialInterval();

                if (currentRetryInterval > retry.getMaxInterval()) {
                    currentRetryInterval = retry.getMaxInterval();
                }
                int currentRetryElapsedTime = retryElapsedTime + currentRetryInterval;
                TimeUtil.sleepSecond(currentRetryInterval);

                httpRequest(outputStr, currentRetryInterval, currentRetryElapsedTime);
            }
            this.LOGGER.error("error: 发送http目的地址:" + this.url + ",网络异常:" + responseCode);
        } catch (Exception e) {
            this.LOGGER.error(e);
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
            threadPool = Executors.newFixedThreadPool(threadNum);
            for (int i = 0; i < threadNum; i++) {
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
            boolean queueIsEmpty = false;
            List<String> list = new ArrayList<>(LIST_SIZE);
            while (true) {
                try {
                    Serializer content = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (content != null) {
                        String contentStr = content.toJson();
                        strLength += contentStr.length();
                        if (strLength >= strLengthLimit) {
                            //如果字符总长度超过了限制，先发送这批trace
                            sendAndClearList(list);
                            strLength = 0;
                            list.add(contentStr);
                            continue;
                        } else {
                            list.add(contentStr);
                        }
                    } else if (queue.isEmpty()) {
                        queueIsEmpty = true;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }

                if (list.size() == LIST_SIZE) {
                    //如果trace的条数超过了预设值，先发送这批trace
                    sendAndClearList(list);
                    strLength = 0;
                }

                if (queueIsEmpty && queue.isEmpty()) {
                    //如果队列已空，发送最后这批trace并
                    if (list.size() != 0) {
                        sendAndClearList(list);
                        strLength = 0;
                    }
                    if (this.isShutDown && this.queue.isEmpty()) {
                        break;
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

