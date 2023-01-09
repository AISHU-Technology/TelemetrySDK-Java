package com.eisoo.telemetry.log;


public class Os {

    private  String description = System.getProperty("os.name");

    private  String type = "linux";

    private  String version = System.getProperty("os.version");


    public Os() {
        String dl = description.toLowerCase();
        if (dl.contains("mac")){
            type = "darwin";
        }else if(dl.contains("windows")){
            type = "windows";
        }
    }
}