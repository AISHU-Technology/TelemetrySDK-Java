### sdk使用方式：

##### 命令行：
$ git clone https://gitlab.aishu.cn/anyrobot/observability/telemetrysdk/telemetryjava.git
$ mvn clean install

##### 在pom.xml里添加：
<dependency>
    <groupId>com.eisoo</groupId>
    <artifactId>SamplerLogger</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>

##### 使用代码
SamplerLogger logger = new SamplerLogger();     //new SamplerLogger对象
logger.setLevel(Level.TRACE);                   //设置日志等级，默认是info
logger.trace("hello world");                    //可以看见打印信息