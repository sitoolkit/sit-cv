package io.sitoolkit.cv.core.infra.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtils {
    private static ObjectWriter objectWriter = new ObjectMapper().writer();

    public static String convertObjectToString(Object src) {
        String value;
        try {
            value = objectWriter.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return value;
    }
}
