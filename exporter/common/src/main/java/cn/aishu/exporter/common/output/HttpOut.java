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
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 655360;
    private final BlockingQueue<Serializer> q1 = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<Serializer> q2 = new LinkedBlockingQueue<>(CAPACITY2);
    private static final int LIST_SIZE = 80;
    private final String serverUrl;
    private boolean isShutDown = false;
    private Retry retry;
    private boolean isGzip = true;

    public HttpOut(String addr, Retry retry, boolean isGzip) {
        serverUrl = addr;
        this.isGzip = isGzip;
        this.retry = retry;
    }

    @Override
    public void send(Serializer logContent) {
        if (isShutDown) {
            return;
        }

        try {
            //超过队列容量直接丢弃日志
            if (q1.size() < CAPACITY) {
                q1.put(logContent);
            } else if (q2.size() < CAPACITY2) {
                q2.put(logContent);
            } else {
                q1.poll();
                q1.put(q2.poll());
                q2.put(logContent);
            }
            //检测是否有活线程，启动线程
            if (threadPool == null || threadPool.isTerminated()) {
                serviceStart();
            }
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
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            //往服务器端写内容
            String arrStr = "[" + outputStr + "]";  //ar的http接收器需要以数组的形式传
            OutputStream outputStream = conn.getOutputStream();
            if (isGzip) {
                outputStream.write(GzipCompressUtil.compress(arrStr).getBytes(StandardCharsets.UTF_8));
            } else {
                outputStream.write(arrStr.getBytes(StandardCharsets.UTF_8));
            }

            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            System.out.println("mycode:" + responseCode);
            if (responseCode == 204 || responseCode == 200) {
                return;
            }
            //当网络不稳定时(TooManyRequests:429, InternalServerError:500, ServiceUnavailable:503)，触发重发机制
            if (retry.isOK(retryElapsedTime, responseCode) && (q2.size() < CAPACITY2)) {
                int currentRetryInterval = retryInterval + retry.getInitialInterval();

                if (currentRetryInterval > retry.getMaxInterval()) {
                    currentRetryInterval = retry.getMaxInterval();
                }
                int currentRetryElapsedTime = retryElapsedTime + currentRetryInterval;
                TimeUtil.sleepSecond(currentRetryInterval);

                httpRequest(outputStr, currentRetryInterval, currentRetryElapsedTime);
            }
            Stdout.println("error: event发送http目的地址:" + serverUrl + ",网络异常:" + responseCode);
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
        int threadNum = 5;
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
            final int strLengthLimit = 5 * 1024 * 1000;
            boolean queueIsEmpty = false;
            List<String> list = new ArrayList<>(LIST_SIZE);
            while (true) {
                Serializer content;
                try {
                    content = q1.poll();
                    if (content == null) {
                        content = q2.poll();
                    }
                    if (content == null) {
                        content = q1.poll(1, TimeUnit.SECONDS);
                    }
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
                    } else if (q1.isEmpty() && q2.isEmpty()) {
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
                if (queueIsEmpty) {
                    //如果队列已空，发送最后这批trace并
                    if (currentSize != 0) {
                        sendAndClearList(list);
                        strLength = 0;
                    }
                    break;
                }
            }
        };
    }

    private int sendAndClearList(List<String> list) {
        httpRequest(String.join(",", list), 0, 0);
        list.clear();
        return 0;
    }
}

