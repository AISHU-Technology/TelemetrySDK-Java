package com.eisoo.telemetry.event;


import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.utils.JsonUtil;

import java.io.IOException;
import java.util.concurrent.*;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    private ExecutorService singleThread = null;
    private static final int CAPACITY = 1024;
    private final BlockingQueue<EventContent> eventQueue = new ArrayBlockingQueue<>(CAPACITY);



    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            while (true) {
                EventContent content;
                try {
                    content = eventQueue.poll(2, TimeUnit.SECONDS);
                    if (content == null) {
                        break;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                dispatch(content);
            }
        };
    }

    /**
     * 启动事件循环
     */
    public void serviceStart() {
        // 创建一个单线程的线程池
        singleThread = Executors.newSingleThreadExecutor();
        singleThread.execute(createThread());
        singleThread.shutdown();

    }

    protected void dispatch(EventContent eventContent) {

        try {
            EventConfig.getDestination().write(JsonUtil.toJson(eventContent));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void dispatchEvent(EventContent eventContent) {
        try {
            //检测是否有活线程，启动线程
            if (singleThread == null || singleThread.isTerminated()) {
                serviceStart();
            }
            //超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(eventContent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
