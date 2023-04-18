package cn.aishu.exporter.common.output;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import javax.net.ssl.HttpsURLConnection;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@RunWith(PowerMockRunner.class)
@PrepareForTest(HttpSender.class)
@PowerMockIgnore({ "javax.net.ssl.*"})
public class HttpSenderTest {
    String httpUrl = "http://localhost/";
    String httpsUrl = "https://localhost/";


    @Test
    public void createTest() {
        Assert.assertNotNull(HttpSender.create(httpUrl));
        Assert.assertNotNull(HttpSender.create(httpUrl, new Retry(), true, 1024));
        Assert.assertNotNull(HttpSender.create(httpsUrl));
        Assert.assertNotNull(HttpSender.create(httpsUrl, new Retry(), true, 1024));
    }

    @Test
    public void httpRequest() throws Exception {
        URL url = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
        HttpURLConnection conn = PowerMockito.mock(HttpURLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(conn);
        OutputStream outputStream = PowerMockito.mock(OutputStream.class);
        PowerMockito.when(conn.getOutputStream()).thenReturn(outputStream);

        PowerMockito.when(conn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAVAILABLE);

        HttpSender hs = new HttpSender(httpUrl, new Retry(true, 1,1,2),true, 2);
        Assert.assertNotNull(hs);
        hs.httpRequest("abc", 1, 1);
        //由于测试开的缓存小，这样测试会有缓冲区溢出的日志打印
        for (int i = 0; i < 10; i++) {
            hs.send(()-> "{\"a\":\"b\"}");
        }

        hs.send(()->null);
        hs.send(()->"");

        hs.shutDown();
        hs.send(()-> "{\"a\":\"b\"}");
    }

    @Test
    public void httpRequestOK() throws Exception {
        URL url = PowerMockito.mock(URL.class);
        PowerMockito.whenNew(URL.class).withAnyArguments().thenReturn(url);
        HttpsURLConnection conn = PowerMockito.mock(HttpsURLConnection.class);
        PowerMockito.when(url.openConnection()).thenReturn(conn);
        OutputStream outputStream = PowerMockito.mock(OutputStream.class);
        PowerMockito.when(conn.getOutputStream()).thenReturn(outputStream);

        PowerMockito.when(conn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);


        HttpSender hs = new HttpSender(httpUrl, new Retry(true, 1,1,2),true, 2);
        Assert.assertNotNull(hs);
        hs.send(()-> "{\"a\":\"b\"}");

    }

}