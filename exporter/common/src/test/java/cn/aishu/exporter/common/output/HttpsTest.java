package cn.aishu.exporter.common.output;

import cn.aishu.exporter.common.utils.TimeUtil;
import com.sun.net.httpserver.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;

public class HttpsTest {
    String httpUrl = "http://localhost/";
    String httpsUrl = "https://localhost/";

    @Test
    public void createTest() {
        Assert.assertNotNull(HttpsSender.create(httpUrl));
        Assert.assertNotNull(HttpsSender.create(httpUrl, new Retry(), true, 1024));
        Assert.assertNotNull(HttpsSender.create(httpsUrl));
        Assert.assertNotNull(HttpsSender.create(httpsUrl, new Retry(), true, 1024));
    }

    @Before
    public void initServer() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        // 创建 http 服务器, 绑定本地 55556 端口
        HttpsServer httpsServer = HttpsServer.create(new InetSocketAddress(55556), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream("src/test/java/cn/aishu/exporter/common/output/testkey3.jks");
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext context = getSSLContext();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);

                } catch (Exception ex) {
                    java.util.logging.Logger.getLogger(getClass().getName()).info("Failed to create HTTPS port");
                }
            }
        });

        // 创建上下文监听, "/" 表示匹配所有 URI 请求
        httpsServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                
                int code = Integer.parseInt(httpExchange.getRequestURI().toString().substring(1));

                // 设置响应头
                httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                httpExchange.sendResponseHeaders(code, -1);

                httpExchange.close();
            }
        });

        // 启动服务
        httpsServer.start();
    }


    @Test
    public void responseCodeTest() {
        HttpsSender hs = new HttpsSender("https://localhost:55556/204", new Retry(true, 1, 1, 2), false, 2);
        hs.httpsRequest("abc", 1, 1);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"a\":\"b\"}");
        for (int i = 0; i < 20; i++) {
            stringBuilder.append(stringBuilder);
        }
        hs.send(()->stringBuilder.toString());
        hs.shutDown();
        hs.send(()-> "{\"a\":\"b\"}");

        HttpsSender hs200 = new HttpsSender("https://localhost:55556/200", new Retry(true, 1, 1, 2), true, 100);
        for (int i = 0; i < 200; i++) {
            hs200.send(() -> "{\"a\":\"b\"}");
        }

        HttpsSender hs429 = new HttpsSender("https://localhost:55556/429", new Retry(true, 1, 1, 1), false, 2);
        hs429.send(() -> "{\"a\":\"b\"}");
        hs429.send(() -> "{\"a\":\"b\"}");

        HttpsSender hs500 = new HttpsSender("https://localhost:55556/500", new Retry(true, 1, 1, 2), false, 2);
        hs500.send(() -> "{\"a\":\"b\"}");

        HttpsSender hs503 = new HttpsSender("https://localhost:55556/503", new Retry(true, 1, 1, 2), false, 2);
        hs503.send(() -> "{\"a\":\"b\"}");
        Assert.assertNotNull(hs503);

        TimeUtil.sleepSecond(2);
    }
}
