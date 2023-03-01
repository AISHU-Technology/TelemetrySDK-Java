package com.eisoo.telemetry.common;

import java.lang.Exception;

import com.eisoo.telemetry.output.StdOut;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public interface Destination {
    void write(SerializeToString toString) throws Exception;

    void shutdown() throws Exception;

    void flush() throws Exception;

    void init(Log log);

    public static Destination getDefaultDestination() {
        Log defaultLog = LogFactory.getLog("defaultDestination");
        StdOut defaultDestination = new StdOut(defaultLog);
        return (Destination) defaultDestination;
    }
}