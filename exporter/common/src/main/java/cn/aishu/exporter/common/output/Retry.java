package cn.aishu.exporter.common.output;


import java.net.HttpURLConnection;

public class Retry {
    // Enabled 是否启用重发机制。
    private  Boolean enabled = true;
    // InitialInterval 第一次重发与上一次发送的时间间隔:单位是秒(s)
    private int initialInterval = 5;
    // MaxInterval 两次重发的最长时间间隔:单位是秒(s)
    private int maxInterval = 15;
    // MaxElapsedTime 重发最长持续的时间:单位是秒(s)
    private int maxElapsedTime = 30;

    public Retry() {
    }

    public static Retry create(Boolean enabled, int initialInterval, Integer maxInterval, int maxElapsedTime){
        return new Retry(enabled, initialInterval, maxInterval, maxElapsedTime);
    }

    public Retry(Boolean enabled, Integer initialInterval, Integer maxInterval, Integer maxElapsedTime) {
        this.enabled = enabled;
        this.initialInterval = initialInterval;
        this.maxInterval = maxInterval;
        this.maxElapsedTime = maxElapsedTime;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public void setInitialInterval(int initialInterval) {
        this.initialInterval = initialInterval;
    }

    public void setMaxInterval(int maxInterval) {
        this.maxInterval = maxInterval;
    }

    public void setMaxElapsedTime(int maxElapsedTime) {
        this.maxElapsedTime = maxElapsedTime;
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
