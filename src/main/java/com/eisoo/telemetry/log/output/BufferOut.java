package com.eisoo.telemetry.log.output;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BufferOut implements Destination {
    private  final int CAPACITY = 1024;
    private  final BlockingQueue<String> bufferQueue = new ArrayBlockingQueue<>(CAPACITY);

    @Override
    public void write(String string) {
        try {
            if(bufferQueue.size() < CAPACITY){
                bufferQueue.put(string);
            }
        } catch (InterruptedException e) {
            //暂时吞掉异常
           Thread.currentThread().interrupt();
        }

    }

    public BlockingQueue<String> getBuffer() {
        return bufferQueue;
    }

}
