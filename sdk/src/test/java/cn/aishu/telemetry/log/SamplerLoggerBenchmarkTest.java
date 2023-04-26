package cn.aishu.telemetry.log;


import cn.aishu.telemetry.log.config.SamplerLogConfig;
import org.junit.Assert;
import org.junit.Test;


public class SamplerLoggerBenchmarkTest {
    @Test
    public void samplerLogBenchmark(){
        SamplerLogConfig.setSender(new BufferOut());
        final Logger logger = LoggerFactory.getLogger("samplerLogBenchmark");

        final long startTime = System.currentTimeMillis();
        final int number = 500000;
        for (int i = 0; i < number; i++) {
            logger.info("test");
        }
        final long endTime = System.currentTimeMillis();
        final long ms = endTime - startTime;
        //基线：1万条日志生成耗时要小于5秒
        final long baseline = number / 1000 * 500;
        Assert.assertTrue(ms < baseline);
        //cps：1秒可生成多少条日志
        final long cps = number / ms * 1000;
        java.util.logging.Logger.getLogger(getClass().getName()).info("*** Benchmark test result: generate "+ number + " log message takes "+ ms + " ms,  cps: " + cps + ". ***");
    }


}

