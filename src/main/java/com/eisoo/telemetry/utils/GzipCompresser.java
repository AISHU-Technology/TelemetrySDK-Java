package com.eisoo.telemetry.utils;

import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.IOException;

class GzipCompresser {
    private GzipCompresser() {

    }

    private static final String GZIP_ENCODE_UTF_8 = "UTF-8";
    private static final String GZIP_ENCODE_ISO_8859_1 = "ISO-8859-1";

    public static byte[] compressData(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return new byte[] {};
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip;
        try {
            gzip = new GZIPOutputStream(out);
            gzip.write(str.getBytes(encoding));
            gzip.close();
        } catch (IOException e) {
            System.err.println("压缩数据失败：" + e.getMessage());
        }
        return out.toByteArray();
    }

    public static String compress(String str) throws IOException {
        return new String(compressData(str, GZIP_ENCODE_UTF_8), GZIP_ENCODE_ISO_8859_1);
    }

    // 这里需要注意的是，这个工具类如果不是发给服务端的，用这个注释掉的压缩方法没有问题，
    // public static byte[] compress(String str) throws IOException {
    // return compressData(str, GZIP_ENCODE_UTF_8);
    // }
}
