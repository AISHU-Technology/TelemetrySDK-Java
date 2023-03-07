package cn.aishu.exporter.common.output;


public interface Sender {
    //发送数据接口
    void send(Serializer serializer);
    //不在接受新的数据，并关闭发送服务
    void shutDown();
}
