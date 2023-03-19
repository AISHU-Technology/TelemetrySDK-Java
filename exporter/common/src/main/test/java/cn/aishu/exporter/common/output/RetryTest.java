package cn.aishu.exporter.common.output;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class RetryTest {

    @Test
    public void createTest() {
        Assert.assertNotNull(Retry.create(true, 1, 1, 1));
    }

    @Test
    public void setGetTest() {
        Retry retry = new Retry();
        retry.setEnabled(true);
        retry.setInitialInterval(1);
        retry.setMaxInterval(1);
        retry.setMaxElapsedTime(1);
        Assert.assertEquals(true, retry.getEnabled());
        Assert.assertEquals(Integer.valueOf(1), retry.getInitialInterval());
        Assert.assertEquals(Integer.valueOf(1), retry.getMaxInterval());
        Assert.assertEquals(Integer.valueOf(1), retry.getMaxElapsedTime());

    }

    @Test
    public void isOK() {
        Retry retry = new Retry();
        Retry.isOK(retry, 1, 204);
    }
}