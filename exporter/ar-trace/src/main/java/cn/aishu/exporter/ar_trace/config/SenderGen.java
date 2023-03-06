//package cn.aishu.exporter.ar_trace.config;
//
//
//
//import cn.aishu.exporter.common.output.*;
//
//import java.util.concurrent.ConcurrentHashMap;
//
//public class SenderGen {
//    private SenderGen() {
//    }
//
//    private static ConcurrentHashMap<String, Sender> senderMap = new ConcurrentHashMap<>();
//
//
//    public static Sender getSender() {
//        String addr = "stdout";
//        if (senderMap.containsKey(addr)){
//            return senderMap.get(addr);
//        }else {
//            Stdout stdout = new Stdout();
//            senderMap.put(addr, stdout);
//            return stdout;
//        }
//    }
//
//    public static Sender getSender(String addr, Retry retry, boolean isGzip) {
//        if (addr == null || addr.isEmpty()) {
//            return getSender();
//        }
//
//        Sender sender = senderMap.get(addr);
//        if (sender != null) {
//            return sender;
//        }
//
//        if (addr.startsWith("https")) {
//            sender = new HttpsOut(addr, retry, isGzip);
//            senderMap.put(addr, sender);
//            return sender;
//        } else if (addr.startsWith("http")) {
//            sender = new HttpOut(addr, retry, isGzip);
//            senderMap.put(addr, sender);
//            return sender;
//        } else {
//            // 若输入的不是以http开头的url地址，则输出到标准输出
//            Stdout.println("traceExporter发送地址设置错误，若发到ar请以http开头，trace数据将输出到标准输出");
//            return getSender();
//        }
//
//    }
//
//}
//
