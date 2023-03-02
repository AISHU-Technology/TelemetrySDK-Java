package cn.aishu.telemetry.common;

import cn.aishu.telemetry.output.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface Output {
    void write(SerializeToString toString) throws Exception;

    void shutdown() throws Exception;

    void flush() throws Exception;

    void init(Log log);

    public static Output getDefaultDestination() {
        Log defaultLog = LogFactory.getLog("defaultDestination");
        StdOut defaultDestination = new StdOut(defaultLog);
        return (Output) defaultDestination;
    }
}