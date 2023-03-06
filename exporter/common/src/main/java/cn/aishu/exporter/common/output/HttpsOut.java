package cn.aishu.exporter.common.output;


import cn.aishu.exporter.common.utils.GzipCompressUtil;
import cn.aishu.exporter.common.utils.TimeUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HttpsOut implements Sender {
    private ExecutorService threadPool = null;
    private static final int CAPACITY = 655360;
    private final BlockingQueue<Serializer> queue = new LinkedBlockingQueue<>(CAPACITY);
    private static final int LIST_SIZE = 80;
    private String serverUrl;
    private boolean isShutDown = false;
    private Retry retry;
    private boolean isGzip = true;

    public HttpsOut(String addr, Retry retry, boolean isGzip) {
        serverUrl = addr;
        this.isGzip = isGzip;
        this.retry = retry;
        try {
            MyX509TrustManager.httpsSupport();
        } catch (Exception e) {
            Stdout.println(e.toString());
        }
    }

    @Override
    public void send(Serializer logContent) {
        if(isShutDown){
            return;
        }

        try {
            //超过队列容量直接丢弃日志
            if (queue.size() < CAPACITY) {
                queue.put(logContent);
            }
            //检测是否有活线程，启动线程
            serviceStart();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Stdout.println(e.toString());
        }
    }

    @Override
    public void shutDown() {
        isShutDown = true;
        threadPool.shutdown();
    }

    public void httpsRequest(String outputStr, int retryInterval, int retryElapsedTime) {
        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        HttpsURLConnection conn = null;
        try {
            URL url = new URL(serverUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            //往服务器端写内容
            String arrStr = "[" + outputStr + "]";  //ar的http接收器需要以数组的形式传
            OutputStream outputStream = conn.getOutputStream();
            if(isGzip){
                outputStream.write(GzipCompressUtil.compress(arrStr).getBytes(StandardCharsets.UTF_8));
            }else {
                outputStream.write(arrStr.getBytes(StandardCharsets.UTF_8));
            }
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 204 || responseCode == 200) {
                return;
            }
            //当网络不稳定时(TooManyRequests:429, InternalServerError:500, ServiceUnavailable:503)，触发重发机制
            if (retry.isOK(retryElapsedTime,  responseCode) && (queue.size() < CAPACITY)) {
                int currentRetryInterval = retryInterval + retry.getInitialInterval();

                if(currentRetryInterval > retry.getMaxInterval()){
                    currentRetryInterval = retry.getMaxInterval();
                }
                int currentRetryElapsedTime = retryElapsedTime + currentRetryInterval;
                TimeUtil.sleepSecond(currentRetryInterval);
                httpsRequest(outputStr, currentRetryInterval, currentRetryElapsedTime);
            }
            Stdout.println("error: event发送https目的地址:" + serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            Stdout.println(e.toString());
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
            int threadNum = 5;
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
            final int strLengthLimit = 5*1024*1000;
            boolean queueIsEmpty = false;
            List<String> list = new ArrayList<>(LIST_SIZE);
            while (true) {
                Serializer content;
                try {
                    content = queue.poll(2, TimeUnit.SECONDS);
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
                int currentSize = list.size();

                if (currentSize == LIST_SIZE) {
                    //如果trace的条数超过了预设值，先发送这批trace
                    sendAndClearList(list);
                    strLength = 0;
                }

                if (queueIsEmpty && queue.isEmpty()) {
                    //如果队列已空，发送最后这批trace并
                    if (currentSize != 0) {
                        sendAndClearList(list);
                        strLength = 0;
                    }
                    if (queue.isEmpty()) {
                        break;
                    }
                }
            }
        };
    }

    private void sendAndClearList(List<String> list) {
        httpsRequest(String.join(",", list), 0,0);
        list.clear();
    }
}

