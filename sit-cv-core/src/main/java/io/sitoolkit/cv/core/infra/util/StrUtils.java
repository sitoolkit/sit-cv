package io.sitoolkit.cv.core.infra.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.Deflater;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import io.sitoolkit.util.buidtoolhelper.UnExpectedException;

public class StrUtils {
    private static Deflater compresser = new Deflater();
    private static ObjectWriter objectWriter = new ObjectMapper().writer();

    public static String compressFilename(String filename) {
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
            throw new UnExpectedException(e);
        }
        return encoded;
    }

    public static String convertToJsonString(Object src) {
        String value;
        try {
            value = objectWriter.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new UnExpectedException(e);
        }
        return value;
    }
}
