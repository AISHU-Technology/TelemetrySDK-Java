package cn.aishu.exporter.common.output;

import cn.aishu.exporter.common.utils.TimeUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;


import java.io.IOException;
import java.net.*;

public class HttpTest {
    static String httpUrl = "http://localhost:55555/";
    static String httpsUrl = "https://localhost/";
    static String hostname = "localhost";


    @Test
    public void createTest() {
        Assert.assertNotNull(HttpSender.create(httpUrl));
        Assert.assertNotNull(HttpSender.create(httpUrl, new Retry(), true, 1024));
        Assert.assertNotNull(HttpSender.create(httpsUrl));
        Assert.assertNotNull(HttpSender.create(httpsUrl, new Retry(), true, 1024));
    }

    static HttpServer httpServer;

    @Test
    public void responseCodeTest() throws IOException {
        HttpSender hs = new HttpSender(httpUrl + 204, new Retry(true, 1,1,2),true, 2);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"a\":\"b\"}");
        for (int i = 0; i < 20; i++) {
            stringBuilder.append(stringBuilder);
        }
        hs.send(()->stringBuilder.toString());
        hs.shutDown();
        hs.send(()-> "{\"a\":\"b\"}");

        HttpSender hs200= new HttpSender(httpUrl + 200, new Retry(true, 1,1,2),false, 2);
        for (int i = 0; i < 30; i++) {
            hs200.send(() -> "{\"a\":\"b\"}");
        }
        HttpSender hs429 = new HttpSender(httpUrl + 429, new Retry(true, 1,1,1),false, 1);
        hs429.send(()-> "{\"a\":\"b\"}");
        hs429.send(()-> "{\"a\":\"b\"}");

        HttpSender hs500 = new HttpSender(httpUrl + 500, new Retry(true, 1,1,2),false, 2);
        hs500.send(()-> "{\"a\":\"b\"}");


        HttpSender hs503 = new HttpSender(httpUrl + 503, new Retry(true, 1, 1, 2), false, 2);
        hs503.send(() -> "{\"a\":\"b\"}");

        Assert.assertNotNull(hs503);
        TimeUtil.sleepSecond(3);
    }

    @BeforeClass
    public static void initServer() throws IOException {
        Integer availablePort = getAvailablePort(55555);
        if (availablePort == null) {
            java.util.logging.Logger.getLogger(HttpTest.class.getName()).info("no AvailablePort");
            return;
        }
        httpUrl = "http://" + hostname + ":" + availablePort + "/";
        // 创建 http 服务器, 绑定本地  端口
        httpServer = HttpServer.create(new InetSocketAddress(availablePort), 0);

        // 创建上下文监听, "/" 表示匹配所有 URI 请求
        httpServer.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {

                URI requestURI = httpExchange.getRequestURI();
                int code = Integer.parseInt(requestURI.toString().substring(1));

                // 设置响应头
                httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
                httpExchange.sendResponseHeaders(code, -1);

                httpExchange.close();
            }
        });

        // 启动服务
        httpServer.setExecutor(null);
        httpServer.start();
        TimeUtil.sleepSecond(3);

    }

    @AfterClass
    public static void stopServer() {
        if (httpServer != null) {
            httpServer.stop(0);
        }
    }

    public static Integer getAvailablePort(Integer from) {
        Integer port = from;
        for (; port < 65533; port++) {
            if (!isHostPortAlive(hostname, port)) {
                return port;
            }
        }
        return null;
    }

    public static boolean isHostPortAlive(String hostName, int port) {
        boolean isAlive = false;

        // 创建一个套接字
        SocketAddress socketAddress = new InetSocketAddress(hostName, port);
        Socket socket = new Socket();

        // 超时设置，单位毫秒
        int timeout = 2000;

        try {
            socket.connect(socketAddress, timeout);
            socket.close();
            isAlive = true;

        } catch (SocketTimeoutException exception) {
            java.util.logging.Logger.getLogger(HttpTest.class.getName()).info("Test SocketTimeoutException " + hostName + ":" + port + ". " + exception.getMessage());
        } catch (IOException exception) {
            java.util.logging.Logger.getLogger(HttpTest.class.getName()).info(
                    "IOException - Unable to connect to " + hostName + ":" + port + ". " + exception.getMessage());
        }
        return isAlive;
    }

}
