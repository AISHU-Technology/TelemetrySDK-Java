package com.eisoo.telemetry.log;


import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.output.BufferOut;

import com.eisoo.telemetry.log.output.Stdout;
import org.junit.Assert;
import org.junit.Test;


public class SamplerLoggerBenchmarkTest {
    @Test
    public void samplerLogBenchmark(){
        SamplerLogConfig.setDestination(new BufferOut());
        final Logger logger = LoggerFactory.getLogger("samplerLogBenchmark");

        final long startTime = System.currentTimeMillis();
        final int number = 10000;
        for (int i = 0; i < number; i++) {
            logger.info("test");
        }
        final long endTime = System.currentTimeMillis();
        final long ms = endTime - startTime;
        //基线：1万条日志生成耗时要小于5秒
        final long baseline = 500 * number / 1000;
        //cps：1秒可生成多少条日志
        final long cps = 1000  * number / ms;
        Assert.assertTrue(ms < baseline);
        new Stdout().write("*** Benchmark test result: generate "+ number + " log message takes "+ ms + " ms,  cps: " + cps + ". ***");
        BufferOut.getBuffer().clear();
    }


}

