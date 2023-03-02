package cn.aishu.telemetry.log;

import cn.aishu.telemetry.log.config.SamplerLogConfig;
import cn.aishu.telemetry.log.utils.JsonUtil;

import java.util.concurrent.*;

public class Dispatcher {

    private static final Dispatcher INSTANCE = new Dispatcher();

    public static Dispatcher getInstance() {
        return INSTANCE;
    }

    private ExecutorService singleThread = null;
    private static final int CAPACITY = 1024;
    private final BlockingQueue<LogContent> eventQueue = new ArrayBlockingQueue<>(CAPACITY);

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

    protected void dispatch(LogContent logContent) {

        SamplerLogConfig.getDestination().write(JsonUtil.toJson(logContent));

    }

    public void dispatchEvent(LogContent logContent) {
        try {
            // 检测是否有活线程，启动线程
            if (singleThread == null || singleThread.isTerminated()) {
                serviceStart();
            }
            // 超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY) {
                eventQueue.put(logContent);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
