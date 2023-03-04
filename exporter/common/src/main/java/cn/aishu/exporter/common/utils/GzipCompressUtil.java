package cn.aishu.exporter.common.utils;


import cn.aishu.exporter.common.output.Stdout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipCompressUtil {

    private GzipCompressUtil() {
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
            Stdout.println("压缩数据失败：" + e.getMessage());
        }
        return out.toByteArray();
    }

    public static String compress(String str) throws IOException {
        return new String(compressData(str, GZIP_ENCODE_UTF_8), GZIP_ENCODE_ISO_8859_1);
    }

}

