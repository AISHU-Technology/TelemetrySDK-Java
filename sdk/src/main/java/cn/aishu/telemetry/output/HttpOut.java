package cn.aishu.telemetry.output;

import cn.aishu.telemetry.common.Output;
import cn.aishu.telemetry.common.SerializeToString;
import cn.aishu.telemetry.utils.TimeUtil;
import cn.aishu.telemetry.utils.MyX509TrustManager;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import org.apache.commons.logging.Log;

public class HttpOut implements Output {

    private ExecutorService threadPool = null;
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 655360;
    private final BlockingQueue<SerializeToString> eventQueue = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<SerializeToString> eventQueue2 = new LinkedBlockingQueue<>(CAPACITY2);
    private static final int LIST_SIZE = 80;
    private final String serverUrl;
    private boolean isHttps;
    private boolean isGziped;
    private Log log;

    public HttpOut(String url) {
        this.serverUrl = url;
        if (this.isHttps) {
            try {
                MyX509TrustManager.httpsSupport();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void init(Log log) {
        this.log = log;
    }

    @Override
    public void write(SerializeToString logContent) {
        try {
            // 超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(logContent);
            } else if (eventQueue2.size() < CAPACITY2) {
                eventQueue2.put(logContent);
            } else {
                log.warn("Buffer limit exceeded,discard data");
                log.debug(eventQueue.poll().toJson());
                eventQueue.put(eventQueue2.poll());
                eventQueue2.put(logContent);
            }
            // 检测是否有活线程，启动线程
            if (threadPool == null || threadPool.isTerminated()) {
                serviceStart();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void flush() throws Exception {
        // 由于一直在进行发送，不存在flush概念
    }

    @Override
    public void shutdown() throws Exception {

    }

    public void httpRequest(String outputStr, int retryInterval) {

        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        HttpURLConnection conn = null;

        try {
            URL url = new URL(serverUrl);
            if (!isHttps) {
                conn = (HttpURLConnection) url.openConnection();
            } else {
                conn = (HttpsURLConnection) url.openConnection();
            }
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            // 往服务器端写内容
            String arrStr = "[" + outputStr + "]"; // ar的http接收器需要以数组的形式传

            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(arrStr.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            log.info("mycode:" + responseCode);
            if (responseCode == 204 || responseCode == 200) {
                return;
            }
            // 当网络不稳定时(TooManyRequests:429, InternalServerError:500,
            // ServiceUnavailable:503)，触发重发机制
            if ((responseCode == 429 || responseCode == 500 || responseCode == 503)
                    && (eventQueue2.size() < CAPACITY2)) {
                int currentRetryInterval = retryInterval + 5;
                TimeUtil.sleepSecond(currentRetryInterval);
                if (retryInterval < 16) {
                    httpRequest(outputStr, currentRetryInterval);
                } else {
                    httpRequest(outputStr, retryInterval);
                }
            }
            throw new RuntimeException("error: event发送http目的地址:" + serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e, e);
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
        int threadNum = 5;
        if (threadPool == null || threadPool.isTerminated()) {
            threadPool = Executors.newFixedThreadPool(threadNum);
            for (int i = 0; i < threadNum; i++) {
                threadPool.submit(createThread());
            }
            threadPool.shutdown();
        }
    }

    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            int strLength = 0;
            final int strLengthLimit = 5 * 1024 * 1000;
            boolean queueIsEmpty = false;
            List<String> list = new ArrayList<>(LIST_SIZE);
            while (true) {
                SerializeToString content;
                try {
                    // 从缓冲队列1或者2中提取数据
                    content = eventQueue.poll();
                    if (content == null) {
                        content = eventQueue2.poll(1, TimeUnit.SECONDS);
                    }
                    if (content == null) {
                        content = eventQueue.poll();
                    }
                    if (content != null) {

                        // dispatcher =
                        // Dispatcher.getInstance(content.getLoggerConfig().getLoggerType());
                        String contentStr = content.toJson();
                        strLength += contentStr.length();
                        if (strLength >= strLengthLimit) {
                            // 如果字符总长度超过了限制，先发送这批trace
                            sendAndClearList(list);
                            strLength = 0;
                            list.add(contentStr);
                            continue;
                        } else {
                            list.add(contentStr);
                        }
                    } else if (eventQueue.isEmpty() && eventQueue2.isEmpty()) {
                        queueIsEmpty = true;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                int currentSize = list.size();

                if (currentSize == LIST_SIZE) {
                    // 如果trace的条数超过了预设值，先发送这批trace
                    sendAndClearList(list);
                    strLength = 0;
                }
                if (queueIsEmpty) {
                    // 如果队列已空，发送最后这批trace并
                    if (currentSize != 0) {
                        sendAndClearList(list);
                        strLength = 0;
                    }
                    // else {
                    // if (dispatcher == null || dispatcher.isTerminated()) {
                    break;
                    // }
                    // }
                }
            }
        };
    }

    private int sendAndClearList(List<String> list) {
        httpRequest("[" + String.join(",", list) + "]", 0);
        list.clear();
        return 0;
    }
}
