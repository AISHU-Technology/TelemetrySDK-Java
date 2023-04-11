package cn.aishu.exporter.common.output;

import cn.aishu.exporter.common.utils.TimeUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URI;

public class HttpTest {
    String httpUrl = "http://localhost/";
    String httpsUrl = "https://localhost/";

    @Test
    public void createTest2() {
        Assert.assertNotNull(HttpSender.create(httpUrl));
        Assert.assertNotNull(HttpSender.create(httpUrl, new Retry(), true, 1024));
        Assert.assertNotNull(HttpSender.create(httpsUrl));
        Assert.assertNotNull(HttpSender.create(httpsUrl, new Retry(), true, 1024));
    }

    HttpServer httpServer;

    @Before
    public void initServer() throws IOException {
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxz");
        // 创建 http 服务器, 绑定本地 55555 端口
        httpServer = HttpServer.create(new InetSocketAddress(55555), 0);

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
        httpServer.start();
        System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyz");
    }

    @After
    public void stopServer(){
        httpServer.stop(0);
        System.out.println("zzzzzzzzzzzzzzzzz");
    }


    @Test
    public void createTest() {
        HttpSender hs = new HttpSender("http://localhost:55555/204", new Retry(true, 1,1,2),true, 2);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"a\":\"b\"}");
        for (int i = 0; i < 20; i++) {
            stringBuilder.append(stringBuilder);
        }
        hs.send(()->stringBuilder.toString());
        hs.shutDown();
        hs.send(()-> "{\"a\":\"b\"}");

        HttpSender hs200= new HttpSender("http://localhost:55555/200", new Retry(true, 1,1,2),false, 100);
        for (int i = 0; i < 200; i++) {
            hs200.send(() -> "{\"a\":\"b\"}");
        }
        HttpSender hs429 = new HttpSender("http://localhost:55555/429", new Retry(true, 1,1,1),false, 1);
        hs429.send(()-> "{\"a\":\"b\"}");
        hs429.send(()-> "{\"a\":\"b\"}");

        HttpSender hs500 = new HttpSender("http://localhost:55555/500", new Retry(true, 1,1,2),false, 2);
        hs500.send(()-> "{\"a\":\"b\"}");

        HttpSender hs503 = new HttpSender("http://localhost:55555/503", new Retry(true, 1,1,2),false, 2);
        hs503.send(()-> "{\"a\":\"b\"}");

        Assert.assertNotNull(hs503);
        TimeUtil.sleepSecond(2);
    }
}
