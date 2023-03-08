package cn.aishu.exporter.common.output;

import java.io.PrintStream;

public class StdSender implements Sender {
    private static final PrintStream out = System.out;

    @Override
    public void send(Serializer logContent) {
        out.println(logContent.toJson());
    }

    @Override
    public void shutDown() {

    }
}
