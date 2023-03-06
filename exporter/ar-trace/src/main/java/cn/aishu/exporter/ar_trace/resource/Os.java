//package cn.aishu.exporter.ar_trace.resource;
//
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class Os {
//
//    private  String description = System.getProperty("os.name");
//
//    private  String type = "linux";
//
//    private  String version = System.getProperty("os.version");
//
//    private static final Os OS = new Os();
//
//
//    public Os() {
//        String dl = description.toLowerCase();
//        if (dl.contains("mac")){
//            type = "darwin";
//        }else if(dl.contains("windows")){
//            type = "windows";
//        }
//    }
//
//    public static Map<String, String> getMap(){
//        HashMap<String, String> map = new HashMap<>();
//        map.put("os.description", Os.OS.description);
//        map.put("os.type", Os.OS.type);
//        map.put("os.version", Os.OS.version);
//        return map;
//    }
//}
