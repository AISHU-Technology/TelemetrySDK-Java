package cn.aishu.telemetry.log;

//日志等级
public enum Level {
    FATAL(50, "Fatal"),
    ERROR(40, "Error"),
    WARN(30, "Warn"),
    INFO(20, "Info"),
    DEBUG(10, "Debug"),
    TRACE(0, "Trace");

    private int levelInt;
    private String levelStr;

    private Level(int i, String s) {
        this.levelInt = i;
        this.levelStr = s;
    }

    public int toInt() {
        return this.levelInt;
    }

    @Override
    public String toString() {
        return this.levelStr;
    }
}
