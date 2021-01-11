package com.sw.vote.util;

import org.apache.commons.io.IOUtils;

import javax.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ZipUtil {

    @Nullable
    public static String compress(@Nullable String data) {
        if (data == null || data.length() == 0) {
            return null;
        }
        try {

            // Create an output stream, and a gzip stream to wrap over.
            ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
            GZIPOutputStream gzip = new GZIPOutputStream(bos);

            // Compress the input string
            gzip.write(data.getBytes(StandardCharsets.UTF_8));
            gzip.close();
            // Convert to base64
            String str = Base64.getEncoder().encodeToString(bos.toByteArray());
            bos.close();
            // return the newly created string
            return str;
        } catch (IOException e) {
            return null;
        }
    }


    @Nullable
    public static String decompress(@Nullable String compressedText) {
        if (compressedText == null || compressedText.length() == 0) {
            return null;
        }
        try {
            // get the bytes for the compressed string
            byte[] compressed = compressedText.getBytes(StandardCharsets.UTF_8);

            // convert the bytes from base64 to normal string
            byte[] asBytes = Base64.getDecoder().decode(compressed);

            ByteArrayInputStream bis = new ByteArrayInputStream(asBytes);
            GZIPInputStream gis = new GZIPInputStream(bis);
            byte[] bytes = IOUtils.toByteArray(gis);
            return new String(bytes, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
