package cn.aishu.exporter.common.output;


import java.net.HttpURLConnection;

public class Retry {
    // Enabled 是否启用重发机制。
    private  Boolean enabled = true;
    // InitialInterval 第一次重发与上一次发送的时间间隔。
    private int initialInterval = 5;
    // MaxInterval 两次重发的最长时间间隔。
    private Integer maxInterval = 15;
    // MaxElapsedTime 重发最长持续的时间。
    private int maxElapsedTime = 30;

    public Retry() {
    }

    public Retry(Boolean enabled, Integer initialInterval, Integer maxInterval, Integer maxElapsedTime) {
        this.enabled = enabled;
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;
        this.maxElapsedTime = maxElapsedTime;
    }

    public static RetryBuilder builder(){
        return new RetryBuilder();
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public Integer getInitialInterval() {
        return initialInterval;
    }

    public Integer getMaxInterval() {
        return maxInterval;
    }

    public Integer getMaxElapsedTime() {
        return maxElapsedTime;
    }

    public static boolean isOK(Retry retry, int retryElapsedTime, int responseCode) {
        return retry.getEnabled() && (retryElapsedTime < retry.getMaxElapsedTime()) && (responseCode ==  429 || responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR || responseCode == HttpURLConnection.HTTP_UNAVAILABLE) ;
    }

}
