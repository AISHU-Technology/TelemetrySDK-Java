package cn.aishu.exporter.common.output;


public interface Sender {
    void send(Serializer serializer);
    void shutDown();
}
