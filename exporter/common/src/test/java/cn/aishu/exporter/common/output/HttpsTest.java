package cn.aishu.exporter.common.output;

import cn.aishu.exporter.common.utils.TimeUtil;
import com.sun.net.httpserver.*;
import org.junit.*;


import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;


public class HttpsTest {
    static String httpUrl = "http://localhost/";
    static String httpsUrl = "https://localhost/";
    static String hostname = "localhost";
    static HttpsServer httpsServer;

    @Test
    public void createTest() {
        Assert.assertNotNull(HttpsSender.create(httpUrl));
        Assert.assertNotNull(HttpsSender.create(httpUrl, new Retry(), true, 1024));
        Assert.assertNotNull(HttpsSender.create(httpsUrl));
        Assert.assertNotNull(HttpsSender.create(httpsUrl, new Retry(), true, 1024));
    }

    @Test
    public void responseCodeTest() throws UnrecoverableKeyException, CertificateException, IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        HttpsSender hs = new HttpsSender(httpsUrl + 204, new Retry(true, 1, 1, 2), false, 2);

        StringBuilder stringBuilder = new StringBuilder();
        String jsonStr = "{\"a\":\"b\"}";
        stringBuilder.append(jsonStr);
        for (int i = 0; i < 20; i++) {
            stringBuilder.append(stringBuilder);
        }
        String longStr = stringBuilder.toString();

        hs.send(() -> longStr);
        hs.shutDown();
        hs.send(() -> jsonStr);

        HttpsSender hs200 = new HttpsSender(httpsUrl + 200, new Retry(true, 1, 1, 2), true, 2);
        for (int i = 0; i < 30; i++) {
            hs200.send(() -> jsonStr);
        }

        HttpsSender hs429 = new HttpsSender(httpsUrl + 429, new Retry(true, 1, 1, 1), false, 2);
        hs429.send(() -> jsonStr);
        hs429.send(() -> jsonStr);

        HttpsSender hs500 = new HttpsSender(httpsUrl + 500, new Retry(true, 1, 1, 2), false, 2);
        hs500.send(() -> jsonStr);

        HttpsSender hs503 = new HttpsSender(httpsUrl + 503, new Retry(true, 1, 1, 2), false, 2);
        hs503.send(() -> jsonStr);
        Assert.assertNotNull(hs503);
        TimeUtil.sleepSecond(5);
    }

    @BeforeClass
    public static void initServer() throws IOException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        Integer availablePort = HttpTest.getAvailablePort(55556);
        if (availablePort == null) {
            java.util.logging.Logger.getLogger(HttpTest.class.getName()).info("no AvailablePort");
            return;
        }
        httpsUrl = "https://" + hostname + ":" + availablePort + "/";
        // 创建 http 服务器, 绑定本地 端口
        httpsServer = HttpsServer.create(new InetSocketAddress(availablePort), 0);
        SSLContext sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = "password".toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");

        FileInputStream fis;
        if (File.separator.equals("\\")) {
            fis = new FileInputStream(".\\src\\test\\resources\\testkey3.jks");
        } else {
            fis = new FileInputStream("./src/test/resources/testkey3.jks");
        }
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
            public void handle(HttpExchange t) throws IOException {
                HttpsExchange httpsExchange = (HttpsExchange) t;

                int code = Integer.parseInt(httpsExchange.getRequestURI().toString().substring(1));

                httpsExchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                httpsExchange.sendResponseHeaders(code, -1);

                InputStream input = httpsExchange.getRequestBody();
                byte[] buffer = new byte[65536];
                int n;

                while ((n = input.read(buffer)) != -1) {
                }

                httpsExchange.close();
            }
        });

        httpsServer.setExecutor(null); // creates a default executor
        httpsServer.start();
        TimeUtil.sleepSecond(3);

    }

    @AfterClass
    public static void stopServer() {
        if (httpsServer != null) {
            httpsServer.stop(0);
        }
    }
}
