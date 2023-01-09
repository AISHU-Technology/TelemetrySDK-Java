package com.eisoo.telemetry.log;


import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.utils.JsonUtil;

import java.util.concurrent.*;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    private ExecutorService singleThread = null;
    private static final int CAPACITY = 4096;
    private static final int CAPACITY2 = 65535;
    private final BlockingQueue<LogContent> eventQueue = new ArrayBlockingQueue<>(CAPACITY);
    private final BlockingQueue<LogContent> eventQueue2 = new LinkedBlockingQueue<>(CAPACITY2);


    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            while (true) {
                LogContent log;
                try {
                    log = eventQueue.poll();
                    if (log == null) {
                        log = eventQueue2.poll(2, TimeUnit.SECONDS);
                    }
                    if (log == null) {
                        log = eventQueue.poll();
                    }
                    if (log == null) {
                        break;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                dispatch(log);
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

    protected void dispatch(LogContent logContent) {

        SamplerLogConfig.getDestination().write(JsonUtil.toJson(logContent));

    }

    public void dispatchEvent(LogContent logContent) {
        try {
            //超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(logContent);
            }else if (eventQueue2.size() < CAPACITY2)  {
                eventQueue2.put(logContent);
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
