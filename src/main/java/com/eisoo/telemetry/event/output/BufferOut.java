package com.eisoo.telemetry.event.output;

import com.eisoo.telemetry.event.EventContent;
import com.eisoo.telemetry.event.config.EventConfig;
import com.eisoo.telemetry.event.utils.JsonUtil;

import java.io.IOException;
import java.util.concurrent.*;

public class BufferOut implements Destination {

    private static final int CAPACITY = 1024;
    private static final BlockingQueue<String> bufferQueue = new ArrayBlockingQueue<>(CAPACITY);

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

    public static BlockingQueue<String> getBuffer() {
        return bufferQueue;
    }

}
