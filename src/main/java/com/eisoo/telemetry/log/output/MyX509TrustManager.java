package com.eisoo.telemetry.log.output;

import javax.net.ssl.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class MyX509TrustManager implements X509TrustManager {
    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType)
            throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

    public static void httpsSupport() throws NoSuchAlgorithmException, NoSuchProviderException, KeyManagementException {
        //创建SSLContext对象，并使用我们指定的信任管理器初始化
        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");
        System.setProperty("https.protocols", "TLSv1.2");

        sslcontext.init(null, new TrustManager[]{new MyX509TrustManager()}, new java.security.SecureRandom());
        HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
            public boolean verify(String host, SSLSession sslsession) {
                return true;
            }
        };
        HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
        HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
    }
}
