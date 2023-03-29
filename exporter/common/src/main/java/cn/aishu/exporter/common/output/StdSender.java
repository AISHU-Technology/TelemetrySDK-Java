package cn.aishu.exporter.common.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.PrintStream;
import java.util.concurrent.*;

public class StdSender implements Sender {
    private static final PrintStream out = System.out;
    private ExecutorService singleThread = null;
    private static final int CAPACITY = 655350;
    private final BlockingQueue<Serializer> eventQueue = new LinkedBlockingQueue<>(CAPACITY);
    private boolean isShutDown = false;
    private final Log logger = LogFactory.getLog(getClass());


    @Override
    public void send(Serializer content) {
        try {
            // 超过队列容量直接丢弃日志
            if (eventQueue.size() < CAPACITY && !this.isShutDown) {
                eventQueue.put(content);
            }else{
                this.logger.warn("缓冲弃区满或sender被关闭，将丢新进数据" + content.toJson());
            }
            // 检测是否有活线程，启动线程
            checkAndStartService();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void shutDown() {
        this.isShutDown = true;
    }

    /**
     * 事件循环逻辑
     */
    Runnable createThread() {
        return () -> {
            while (true) {
                Serializer log;
                try {
                    log = eventQueue.poll(2, TimeUnit.SECONDS);
                    if (log == null) {
                        break;
                    }
                } catch (InterruptedException ie) {
                    this.logger.error("telemetry 标准输出启动失败", ie);
                    Thread.currentThread().interrupt();
                    return;
                }
                out.println(log.toJson());
            }
        };
    }

    /**
     * 启动事件循环
     */
    public synchronized void checkAndStartService() {
        // 创建一个单线程的线程池
        if (singleThread == null || singleThread.isTerminated()) {
            singleThread = Executors.newSingleThreadExecutor();
            singleThread.execute(createThread());
            singleThread.shutdown();
        }
    }
}
