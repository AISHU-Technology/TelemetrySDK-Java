package com.eisoo.telemetry.log.output;


import com.eisoo.telemetry.log.Dispatcher;
import com.eisoo.telemetry.log.utils.SleepUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


public class HttpOut implements Destination {
    private ExecutorService threadPool = null;
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 655360;
    private final BlockingQueue<String> eventQueue = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<String> eventQueue2 = new LinkedBlockingQueue<>(CAPACITY2);
    private static final int LIST_SIZE = 80;
    private final String serverUrl;

    public HttpOut(String url) {
        serverUrl = url;
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

    public void httpRequest(String outputStr, int repeat) {
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
            outputStream.write(arrStr.getBytes(StandardCharsets.UTF_8));
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
                if (repeat < 16) {
                    httpRequest(outputStr, rep);
                } else {
                    httpRequest(outputStr, repeat);
                }
            }
            throw new RuntimeException("error: event发送http目的地址:" + serverUrl + ",网络异常:" + responseCode);
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
            List<String> list = new ArrayList<>(LIST_SIZE);
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
                            sendAndClearList(list);
                            strLength = 0;
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

                if (currentSize == LIST_SIZE) {
                    sendAndClearList(list);
                    strLength = 0;
                }
                if (queueIsEmpty) {
                    if (currentSize != 0) {
                        sendAndClearList(list);
                        strLength = 0;
                    } else if (Dispatcher.getInstance().isTerminated()) {
                        break;
                    }
                }
            }
        };
    }

    private int sendAndClearList(List<String> list) {
        httpRequest(String.join(",", list), 0);
        list.clear();
        return 0;
    }
}

