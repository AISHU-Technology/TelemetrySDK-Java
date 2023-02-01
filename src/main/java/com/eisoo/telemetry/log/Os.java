package com.eisoo.telemetry.log;


public class Os {

    private  String description = System.getProperty("os.name");

    private  String type = "linux";

    private  String version = System.getProperty("os.version");

    private static final Os OS = new Os();

    public static Os getOs(){
        return OS;
    }

    public Os() {
        String dl = description.toLowerCase();
        if (dl.contains("mac")){
            type = "darwin";
        }else if(dl.contains("windows")){
            type = "windows";
        }
    }
}
