package cn.aishu.exporter.common.output;

import org.junit.Assert;
import org.junit.Test;

import java.security.cert.CertificateException;


public class MyX509TrustManagerTest {
    MyX509TrustManager myX509TrustManager = new MyX509TrustManager();

    @Test
    public void checkClientTrusted() throws CertificateException {
       myX509TrustManager.checkClientTrusted(null, "");
    }

    @Test
    public void checkServerTrusted() throws CertificateException {
        myX509TrustManager.checkServerTrusted(null, "");
    }

    @Test
    public void getAcceptedIssuers() {
        Assert.assertNotNull(myX509TrustManager.getAcceptedIssuers());
    }
}