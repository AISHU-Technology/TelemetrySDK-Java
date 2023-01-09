package com.eisoo.telemetry.event.output;

import com.eisoo.telemetry.event.Dispatcher;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;


public class HttpsOut implements Destination {

    private String serverUrl = null;

    public HttpsOut(String url) {
        serverUrl = url;
        try {
            MyX509TrustManager.httpsSupport();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void write(String str) {
        httpsRequest(str, 0);
    }

    public void httpsRequest(String outputStr, int repeat) {
        if (outputStr == null || outputStr.isEmpty()) {
            return;
        }
        HttpsURLConnection conn = null;
        try {
            URL url = new URL(serverUrl);
            conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "Application/json");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.connect();

            //往服务器端写内容
            String arrStr = "[" + outputStr + "]";  //ar的http接收器需要以数组的形式传
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(arrStr.getBytes("utf-8"));
            outputStream.flush();
            outputStream.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == 204 || responseCode == 200) {
                return;
            }

            //当网络不稳定时(TooManyRequests:429, InternalServerError:500, ServiceUnavailable:503)，触发重发机制
            if ((responseCode == 429 || responseCode == 500 || responseCode == 503) && !Dispatcher.getInstance().isCacheFull()) {
                int rep = repeat + 5;
                try {
                    Thread.sleep(1000 * rep);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (repeat < 16){
                    httpsRequest(outputStr, rep);
                }else{
                    httpsRequest(outputStr, repeat);
                }
            }
            throw new RuntimeException("error: event发送https目的地址:" + serverUrl + ",网络异常:" + responseCode);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }
}

