package cn.aishu.exporter.common.output;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Log4jSender implements Sender {

    @Override
    public void send(Serializer logContent) {
        log.info(logContent.toJson());
    }

    @Override
    public void shutDown() {
        // 对于标准输出不需要实现
    }
}
