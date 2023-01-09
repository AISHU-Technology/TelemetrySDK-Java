package com.eisoo.telemetry.event;


import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.utils.JsonUtil;

import java.util.concurrent.*;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    private ExecutorService singleThread = null;
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 65535;
    private final BlockingQueue<EventContent> eventQueue = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<EventContent> eventQueue2 = new LinkedBlockingQueue<>(CAPACITY2);

    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            while (true) {
                EventContent content;
                try {
                    content = eventQueue.poll();
                    if (content == null) {
                        content = eventQueue2.poll(2, TimeUnit.SECONDS);
                    }
                    if (content == null) {
                        content = eventQueue.poll();
                    }
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
    public synchronized void serviceStart() {
        // 创建一个单线程的线程池
        if (singleThread == null || singleThread.isTerminated()) {
            singleThread = Executors.newSingleThreadExecutor();
            singleThread.execute(createThread());
            singleThread.shutdown();
        }
    }

    protected void dispatch(EventContent eventContent) {
        EventConfig.getDestination().write(JsonUtil.toJson(eventContent));
    }

    public void dispatchEvent(EventContent eventContent) {
        try {
            //超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(eventContent);
            } else if (eventQueue2.size() < CAPACITY2) {
                eventQueue2.put(eventContent);
            }
            //检测是否有活线程，启动线程
            if (singleThread == null || singleThread.isTerminated()) {
                serviceStart();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public boolean isCacheFull(){
        return eventQueue2.size() >= CAPACITY2;
    }
}
