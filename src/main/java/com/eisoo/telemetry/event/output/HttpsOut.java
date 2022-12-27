package com.eisoo.telemetry.event.output;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import java.io.*;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import static com.sun.webkit.network.URLs.newURL;


public class HttpsOut implements Destination {

    private String serverUrl = null;

    public HttpsOut(String url){
        serverUrl = url;
        try {
            MyX509TrustManager.httpsSupport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(String str) {
        String res = httpsRequest(serverUrl, "POST", str);
        System.out.println(res);
    }

    public static String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        StringBuffer buffer = null;
        OutputStream outputStream = null;
        InputStream inputStream = null;
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod(requestMethod);
            conn.connect();

            String arrStr = "[" + outputStr + "]";  //ar的http接收器需要以数组的形式传
//            System.out.println(arrStr);
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
                    throw new RuntimeException(e);
                }
            }
            if(inputStream !=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }
        return buffer.toString();
    }

}

