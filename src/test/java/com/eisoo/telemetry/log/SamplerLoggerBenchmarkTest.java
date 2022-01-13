package com.eisoo.telemetry.log;

import com.eisoo.telemetry.log.config.SamplerLogConfig;
import com.eisoo.telemetry.log.output.BufferOut;

import org.junit.Assert;
import org.junit.Test;


public class SamplerLoggerBenchmarkTest {
    @Test
    public void samplerLogBenchmark(){
        SamplerLogConfig.setDestination(new BufferOut());
        final Logger logger = LoggerFactory.getLogger("samplerLogBenchmark");

        final long startTime = System.currentTimeMillis();
        final int number = 1000000;
        for (int i = 0; i < number; i++) {
            logger.info("test");
        }
        final long endTime = System.currentTimeMillis();
        final long ms = endTime - startTime;
        final long baseline = number / 1000 * 5;
        final long cps = 1000  * number / ms;
        Assert.assertTrue(ms < baseline);
        System.out.println("*** Benchmark test result: generate "+ number + " log message takes "+ ms + " ms,  cps: " + cps + ". ***");
    }


}

