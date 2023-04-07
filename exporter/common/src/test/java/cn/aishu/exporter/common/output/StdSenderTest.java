package cn.aishu.exporter.common.output;

import org.junit.Test;

import static org.junit.Assert.*;

public class StdSenderTest {

    @Test
    public void send() {
        StdSender stdSender = new StdSender();
        stdSender.send(()->{return "";});
        stdSender.shutDown();
    }

}