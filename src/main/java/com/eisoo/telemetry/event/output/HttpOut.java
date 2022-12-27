package com.eisoo.telemetry.event.output;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;


public class HttpOut implements Destination {
    private String serverUrl = null;


    public HttpOut(String url){
        serverUrl = url;
    }

    @Override
    public void write(String str) {
        String res = httpRequest(serverUrl, "POST", str);
        System.out.println(res);
    }

    public static String httpRequest(String requestUrl, String requestMethod, String outputStr) {
        StringBuffer buffer = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.connect();

            String arrStr = "[" + outputStr + "]";  //ar的http接收器需要以数组的形式传
            //往服务器端写内容
            if (null != outputStr) {
                outputStream = conn.getOutputStream();
                outputStream.write(arrStr.getBytes("utf-8"));
                outputStream.flush();
                outputStream.close();
            }
            //读取服务器端返回的内容
            inputStream = conn.getInputStream();
            InputStreamReader isr = new InputStreamReader(inputStream, "utf-8");
            BufferedReader br = new BufferedReader(isr);
            buffer = new StringBuffer();
            String line = null;
            while ((line = br.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(outputStream !=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(inputStream !=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return buffer.toString();
    }

}

