package cn.aishu.exporter.common.output;


import cn.aishu.exporter.common.utils.GzipCompressUtil;
import cn.aishu.exporter.common.utils.TimeUtil;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class HttpOut implements Sender {

    private ExecutorService threadPool = null;
    private static final int CAPACITY = 655360;
    private final BlockingQueue<Serializer> queue = new LinkedBlockingQueue<>(CAPACITY);
    private static final int LIST_SIZE = 80;
    private final String serverUrl;
    private boolean isShutDown = false;
    private Retry retry = new Retry();
    private boolean isGzip = true;

    public HttpOut(String addr, Retry retry, boolean isGzip) {
        this.serverUrl = addr;
        this.isGzip = isGzip;
        if(retry != null){
            this.retry = retry;
        }
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

    public void httpRequest(String outputStr, int retryInterval, int retryElapsedTime) {
        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        HttpURLConnection conn = null;
        try {
            URL url = new URL(serverUrl);
            conn = (HttpURLConnection) url.openConnection();
            if (isGzip){
                conn.setRequestProperty("Content-Type", "Application/octet-stream");
                conn.setRequestProperty("Content-Encoding", "gzip");
            }else {
                conn.setRequestProperty("Content-Type", "Application/json");
            }
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            //往服务器端写内容
            OutputStream outputStream = conn.getOutputStream();
            if (isGzip) {
                outputStream.write(GzipCompressUtil.compress(outputStr).getBytes(StandardCharsets.UTF_8));
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
            Stdout.println("error: 发送http目的地址:" + serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            Stdout.println("发送http" + e.toString());
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
            final int strLengthLimit = 5 * 1024 * 1000;
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
        httpRequest("[" + String.join(",", list) + "]", 0, 0);


        list.clear();
    }
}

