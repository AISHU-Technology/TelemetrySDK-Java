package cn.aishu.telemetry.log;

import cn.aishu.exporter.common.output.Sender;
import cn.aishu.exporter.common.output.Serializer;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;


public class BufferOut implements Sender {
    private  static final int CAPACITY = 1024;
    private  final BlockingQueue<String> bufferQueue = new ArrayBlockingQueue<>(CAPACITY);


    public BlockingQueue<String> getBuffer() {
        return bufferQueue;
    }

    @Override
    public void send(Serializer serializer) {
        try {
            if(bufferQueue.size() < CAPACITY){
                bufferQueue.put(serializer.toJson());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Logger.getLogger(getClass().getName()).warning(e.toString());
        }
    }

    @Override
    public void shutDown() {
        Logger.getLogger(getClass().getName()).info("bufferOut is shutdown");
    }
}
