package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private JsonUtils() {
    }

    public static String obj2str(Object obj) {
        try {
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> T str2obj(String str, Class<T> objType) {
        try {
            return mapper.readValue(str, objType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
