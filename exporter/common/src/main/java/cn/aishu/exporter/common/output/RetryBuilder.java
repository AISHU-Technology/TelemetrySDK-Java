package cn.aishu.exporter.common.output;


public class RetryBuilder {
    // Enabled 是否启用重发机制。
    private  Boolean enabled = true;
    // InitialInterval 第一次重发与上一次发送的时间间隔。单位是秒
    private int initialInterval = 5;
    // MaxInterval 两次重发的最长时间间隔。
    private Integer maxInterval = 15;
    // MaxElapsedTime 重发最长持续的时间。
    private int maxElapsedTime = 30;


    public RetryBuilder setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public RetryBuilder setInitialInterval(int initialInterval) {
        this.initialInterval = initialInterval;
        return this;
    }

    public RetryBuilder setMaxInterval(Integer maxInterval) {
        this.maxInterval = maxInterval;
        return this;
    }

    public RetryBuilder setMaxElapsedTime(int maxElapsedTime) {
        this.maxElapsedTime = maxElapsedTime;
        return this;
    }

    public Retry build(){
        return new Retry(enabled, initialInterval, maxInterval, maxElapsedTime);
    }
}
