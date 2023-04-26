package cn.aishu.exporter.ar_metric;


import org.junit.Assert;
import org.junit.Test;

public class AnyrobotScopeTest{

    @Test
    public void testTestName() {
        String name = "abcName";
        AnyrobotScope anyrobotScope = new AnyrobotScope();
        anyrobotScope.setName(name);
        Assert.assertEquals(name, anyrobotScope.getName());
    }

    @Test
    public void testVersion() {
        String version = "123Version";
        AnyrobotScope anyrobotScope = new AnyrobotScope();
        anyrobotScope.setVersion(version);
        Assert.assertEquals(version, anyrobotScope.getVersion());
    }

    @Test
    public void testSchemaURL() {
        String schemaURL = "httpsSchemaURL";
        AnyrobotScope anyrobotScope = new AnyrobotScope();
        anyrobotScope.setSchemaURL(schemaURL);
        Assert.assertEquals(schemaURL, anyrobotScope.getSchemaURL());
    }

}