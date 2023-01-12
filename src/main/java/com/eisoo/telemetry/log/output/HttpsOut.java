package com.eisoo.telemetry.log.output;


import com.eisoo.telemetry.log.Dispatcher;
import com.eisoo.telemetry.log.utils.SleepUtil;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class HttpsOut implements Destination {
    private ExecutorService threadPool = null;
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 655360;
    private final BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<String> eventQueue2 = new LinkedBlockingQueue<>(CAPACITY2);
    private final int listSize = 80;
    private String serverUrl = null;

    public HttpsOut(String url) {
        serverUrl = url;
        try {
            MyX509TrustManager.httpsSupport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(String eventContent) {
        try {
            //超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(eventContent);
            } else if (eventQueue2.size() < CAPACITY2) {
                eventQueue2.put(eventContent);
            }
            //检测是否有活线程，启动线程
            if (threadPool == null || threadPool.isTerminated()) {
                serviceStart();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void httpsRequest(String outputStr, int repeat) {
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
            outputStream.write(arrStr.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();
            if (responseCode == 204 || responseCode == 200) {
                return;
            }
            //当网络不稳定时(TooManyRequests:429, InternalServerError:500, ServiceUnavailable:503)，触发重发机制
            if ((responseCode == 429 || responseCode == 500 || responseCode == 503) && (eventQueue2.size() < CAPACITY2)) {
                int rep = repeat + 5;
                SleepUtil.second(rep);
                if (repeat < 16){
                    httpsRequest(outputStr, rep);
                }else{
                    httpsRequest(outputStr, repeat);
                }
            }
            throw new RuntimeException("error: event发送https目的地址:" + serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
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
            final int strLengthLimit = 5*1024*1000;
            boolean queueIsEmpty = false;
            List<String> list = new ArrayList<>(listSize);
            while (true) {
                String content;
                try {
                    content = eventQueue.poll();
                    if (content == null) {
                        content = eventQueue2.poll(1, TimeUnit.SECONDS);
                    }
                    if (content == null) {
                        content = eventQueue.poll();
                    }
                    if (content != null) {
                        strLength += content.length();
                        if (strLength >= strLengthLimit){
                            strLength = sendAndClearList(list);
                            list.add(content);
                            continue;
                        }else {
                            list.add(content);
                        }
                    } else if(eventQueue.isEmpty() && eventQueue2.isEmpty()){
                        queueIsEmpty = true;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                int currentSize = list.size();

                if (currentSize == listSize) {
                    strLength =sendAndClearList(list);
                }
                if (queueIsEmpty) {
                    if (currentSize != 0) {
                        strLength =sendAndClearList(list);
                    } else if (Dispatcher.getInstance().isTerminated()) {
                        break;
                    }
                }
            }
        };
    }

    private int sendAndClearList(List<String> list) {
        httpsRequest(String.join(",", list), 0);
        list.clear();
        return 0;
    }
}

