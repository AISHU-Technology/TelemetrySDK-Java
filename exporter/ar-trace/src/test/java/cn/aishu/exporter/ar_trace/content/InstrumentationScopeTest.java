package cn.aishu.exporter.ar_trace.content;


import io.opentelemetry.sdk.common.InstrumentationScopeInfo;

import org.junit.Assert;
import org.junit.Test;

public class InstrumentationScopeTest {
    @Test
    public void testConstruct(){
        Assert.assertNotNull(new InstrumentationScope(null));
        Assert.assertNotNull(new InstrumentationScope(InstrumentationScopeInfo.empty()));
    }

}