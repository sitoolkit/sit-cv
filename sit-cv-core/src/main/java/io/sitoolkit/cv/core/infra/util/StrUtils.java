package io.sitoolkit.cv.core.infra.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.Deflater;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StrUtils {
    private static Deflater compresser = new Deflater();

    public static String compressAsFilename(String filename) {
        String encoded = null;
        byte[] dataByte = filename.getBytes();

        compresser.reset();
        compresser.setLevel(Deflater.BEST_COMPRESSION);
        compresser.setInput(dataByte);
        compresser.finish();

        try(ByteArrayOutputStream baos = new ByteArrayOutputStream(dataByte.length)){
            byte[] buf = new byte[1024];
            while(!compresser.finished()) {
                int compByte = compresser.deflate(buf);
                baos.write(buf, 0, compByte);
            }
            byte[] compData = baos.toByteArray();
            encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(compData);
        } catch (IOException e) {
            log.warn("IOException", e);
        }
        return encoded;
    }
}
