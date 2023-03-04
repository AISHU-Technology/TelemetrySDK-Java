package cn.aishu.exporter.common.output;


import java.io.PrintStream;

public class Stdout implements Sender {
    private static final PrintStream out = System.out;

    public static void println(String string) {
        out.println(string);
    }

    @Override
    public void send(Serializer logContent) {
        out.println(logContent.toJson());
    }

    @Override
    public void shutDown() {

    }
}
