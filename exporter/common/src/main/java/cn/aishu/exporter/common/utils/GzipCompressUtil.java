package cn.aishu.exporter.common.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipCompressUtil {

    private final Log logger = LogFactory.getLog(getClass());

    public static byte[] compressData(String str, String encoding) {
        if (str == null || str.length() == 0) {
            return new byte[] {};
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (GZIPOutputStream gzip = new GZIPOutputStream(out)) {
            gzip.write(str.getBytes(encoding));
        } catch (IOException e) {
            GzipCompressUtil gzipCompressUtil = new GzipCompressUtil();
            gzipCompressUtil.logger.error("压缩数据失败：" + e.getMessage());
        }
        return out.toByteArray();
    }

}
