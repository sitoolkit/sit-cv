package io.sitoolkit.cv.tools.infra;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static <T> T url2obj(URL url, Class<T> objType) {
        try {
            return MAPPER.readValue(url, objType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
