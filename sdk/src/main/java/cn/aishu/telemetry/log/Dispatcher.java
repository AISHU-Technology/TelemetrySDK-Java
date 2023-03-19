package cn.aishu.telemetry.log;


import cn.aishu.telemetry.log.config.SamplerLogConfig;

import java.util.concurrent.*;


public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    private ExecutorService singleThread = null;
    private static final int CAPACITY = 65535;
    private final BlockingQueue<LogContent> eventQueue = new LinkedBlockingQueue<>(CAPACITY);

    public static Dispatcher getInstance() {
        return INSTANCE;
    }


    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            while (true) {
                LogContent log;
                try {
                    log = eventQueue.poll(2, TimeUnit.SECONDS);
                    if (log == null) {
                        break;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                dispatch(log);
            }
            SamplerLogConfig.getSender().shutDown();
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
        SamplerLogConfig.getSender().send(logContent);
    }

    public void dispatchEvent(LogContent logContent) {
        try {
            // 超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(logContent);
            }
            // 检测是否有活线程，启动线程
            if (singleThread == null || singleThread.isTerminated()) {
                serviceStart();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}
