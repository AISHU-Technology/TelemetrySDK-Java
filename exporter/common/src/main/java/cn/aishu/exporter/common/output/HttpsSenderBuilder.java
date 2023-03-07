package cn.aishu.exporter.common.output;


public class HttpsSenderBuilder {
    // Enabled 是否启用重发机制。
    private  String url;

    // InitialInterval 第一次重发与上一次发送的时间间隔。单位是秒
    private Retry retry = new Retry();
    // MaxInterval 两次重发的最长时间间隔。
    private boolean isGzip = true;

    private  int cacheCapacity = 65535;


    public HttpsSenderBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpsSenderBuilder setCacheCapacity(int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        return this;
    }

    public HttpsSenderBuilder setRetry(Retry retry) {
        this.retry = retry;
        return this;
    }

    public HttpsSenderBuilder setGzip(boolean gzip) {
        isGzip = gzip;
        return this;
    }


    public HttpsSender build(){
        return new HttpsSender(url, retry, isGzip, cacheCapacity);
    }
}
