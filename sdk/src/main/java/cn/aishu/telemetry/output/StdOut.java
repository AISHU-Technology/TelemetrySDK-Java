package cn.aishu.telemetry.output;

import java.io.PrintStream;
import org.apache.commons.logging.Log;

import cn.aishu.telemetry.common.Output;
import cn.aishu.telemetry.common.SerializeToString;

public class StdOut implements Output {
    private final PrintStream out = System.out;
    private Log log;
    boolean needLogging;

    public PrintStream getOut() {
        return this.out;
    }

    public Log getLog() {
        return this.log;
    }

    public boolean getNeedLogging() {
        return this.needLogging;
    }

    public void setNeedLogging(boolean needLogging) {
        this.needLogging = needLogging;
    }

    public void write(String string) {
        out.println(string);
    }

    public void init(Log log) {
        this.log = log;
    }

    public void flush() throws Exception {
        // stdout不存在缓存，因此空置实现
    }

    public void shutdown() throws Exception {
        // stdout不存在停机，因此空置实现
    }

    @Override
    public void write(SerializeToString logContent) {
        out.println(logContent.toJson());
        if (this.needLogging) {
            log.info(logContent.toJson());
        }
    }

    public StdOut(Log log) {
        this.log = log;
        this.needLogging = false;
    }
}