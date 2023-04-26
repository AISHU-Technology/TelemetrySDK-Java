package cn.aishu.exporter.common.output;


import cn.aishu.exporter.common.utils.GzipCompressUtil;
import cn.aishu.exporter.common.utils.TimeUtil;

import okhttp3.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.*;


public class HttpSender implements Sender {
    private static final int THREAD_NUM = 5;
    public static final int STR_LIST_SIZE_LIMIT = 80;
    public static final int STR_LENGTH_LIMIT = 5 * 1024 * 1000;  // 5MB

    private ExecutorService threadPool = null;
    private int capacity = 655350;
    private final BlockingQueue<Serializer> queue = new LinkedBlockingQueue<>(capacity);
    private String serverUrl;
    private boolean isShutDown = false;
    private Retry retry = new Retry();
    private boolean isGzip = false;
    private final Log logger = LogFactory.getLog(getClass());
    private OkHttpClient client;
    private MediaType mediaType;


    public static Sender create(String url) {
        return new HttpSender(url);
    }

    public static Sender create(String url, Retry retry, boolean isGzip, int cacheCapacity) {
        return new HttpSender(url, retry, isGzip, cacheCapacity);
    }

    public HttpSender(String url) {
        this.serverUrl = url;

        // 创建发送数据的httpClient
        createClient();
    }

    public HttpSender(String url, Retry retry, boolean isGzip, int cacheCapacity) {
        this.serverUrl = url;
        this.isGzip = isGzip;
        if (retry != null) {
            this.retry = retry;
        }
        this.capacity = cacheCapacity;

        // 创建发送数据的httpClient
        createClient();
    }

    @Override
    public void send(Serializer logContent) {
        if (isShutDown) {
            return;
        }

        try {
            checkAndCreateThreadPool();
            if (queue.size() < capacity) {
                queue.put(logContent);
            } else {
                // 超过队列容量直接丢弃日志
                this.logger.warn("缓冲弃区满，将丢新进数据" + logContent.toJson());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            this.logger.error(e);
        }
    }

    @Override
    public void shutDown() {
        isShutDown = true;
    }

    public void httpRequest(String outputStr, int retryInterval, int retryElapsedTime) {
        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        // 往服务器端写内容

        RequestBody body;
        if (isGzip) {
            body = RequestBody.create(GzipCompressUtil.compressData(outputStr, StandardCharsets.UTF_8.toString()), mediaType);
        } else {
            body = RequestBody.create(outputStr.getBytes(StandardCharsets.UTF_8), mediaType);
        }

        Request request = new Request.Builder()
                .url(serverUrl)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            int responseCode = response.code();
            if (responseCode == HttpURLConnection.HTTP_NO_CONTENT || responseCode == HttpURLConnection.HTTP_OK) {
                return;
            } else {
                this.logger.error(response.message());
            }

            // 当网络不稳定时(TooManyRequests:429, InternalServerError:500,
            // ServiceUnavailable:503)，触发重发机制
            if (Retry.isOK(retry, retryElapsedTime, responseCode) && (queue.size() < capacity)) {
                int currentRetryInterval = retryInterval + retry.getInitialInterval();
                if (currentRetryInterval > retry.getMaxInterval()) {
                    currentRetryInterval = retry.getMaxInterval();
                }
                int currentRetryElapsedTime = retryElapsedTime + currentRetryInterval;
                TimeUtil.sleepSecond(currentRetryInterval);

                httpRequest(outputStr, currentRetryInterval, currentRetryElapsedTime);
            }
            this.logger.error("error: 发送http目的地址:" + this.serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            this.logger.error(serverUrl + "http send error:", e);
        }
    }

    private void createClient() {
        if (isGzip) {
            mediaType = MediaType.get("Content-Encoding/gzip; charset=utf-8");
        } else {
            mediaType = MediaType.get("application/json; charset=utf-8");
        }
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new MyX509TrustManager()};

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            int maxIdleConnections = THREAD_NUM;
            long keepAliveDuration = 30L;
            TimeUnit timeUnit = TimeUnit.SECONDS;

            ConnectionPool connectionPool = new ConnectionPool(maxIdleConnections, keepAliveDuration, timeUnit);

            this.client = new OkHttpClient.Builder()
                    .connectionPool(connectionPool)
                    .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                    .hostnameVerifier((hostname, session) -> true)
                    .build();
        } catch (Exception e) {
            this.logger.error(e.getMessage());
        }
    }

    /**
     * 启动事件循环
     */
    public synchronized void checkAndCreateThreadPool() {
        // 创建一个线程池的线程池
        if (threadPool == null || threadPool.isTerminated()) {
            threadPool = Executors.newFixedThreadPool(THREAD_NUM);
            for (int i = 0; i < THREAD_NUM; i++) {
                threadPool.submit(createWorker());
            }
            threadPool.shutdown();
        }
    }

    private void sendAndClearList(List<String> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        httpRequest("[" + String.join(",", list) + "]", 0, 0);
        list.clear();
    }

    Runnable createWorker() {
        return () -> {
            SerializerContainer container = new SerializerContainer();
            boolean loop = true;
            while (loop) {
                try {
                    // 获取queue中的数据，并判断是否马上发送这批数据
                    if (container.putAndShouldSend(queue.poll(100, TimeUnit.MILLISECONDS))) {
                        sendAndClearList(container.getList());
                        continue;
                    }
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                if (container.isGetNull()) {
                    // 此次循环没有获取到数据，把之前缓存的数据先发送出去，防止数据滞留不发
                    sendAndClearList(container.getList());
                    if (this.queue.isEmpty()) {
                        loop = false;
                    }
                }
            }
        };
    }
}

