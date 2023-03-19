package cn.aishu.telemetry.log.constant;

public enum KeyConstant {
    MESSAGE("Message"),
    TYPE("Type"),
    CONFIG_FILE("slog.properties");

    private final String key;
    KeyConstant(String k){
        this.key = k;
    }

    @Override
    public String toString() {
        return this.key;
    }

}
