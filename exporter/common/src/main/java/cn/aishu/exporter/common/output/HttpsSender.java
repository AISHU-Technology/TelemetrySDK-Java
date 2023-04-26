package cn.aishu.exporter.common.output;

//该类已不建议使用，请直接使用HttpSender，兼容http和https协议
public class HttpsSender implements Sender {
    private Sender sender;
    public static Sender create(String url){
        return new HttpsSender(url);
    }

    public static Sender create(String url, Retry retry, boolean isGzip, int cacheCapacity){
        return new HttpsSender(url, retry, isGzip, cacheCapacity);
    }

    public HttpsSender(String url) {
        this.sender = new HttpSender(url);
    }

    public HttpsSender(String url, Retry retry, boolean isGzip, int cacheCapacity) {
        this.sender = new HttpSender(url, retry, isGzip, cacheCapacity);
    }

    @Override
    public void send(Serializer serializer) {
        this.sender.send(serializer);
    }

    @Override
    public void shutDown() {
        this.sender.shutDown();
    }
}
