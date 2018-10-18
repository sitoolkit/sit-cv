package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public abstract class ReportModel {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final ObjectWriter objectWriter = new ObjectMapper().writer();

    abstract public void write(File outputDir);

    protected String convertObjectToJsonString(Object src) {
        String value;
        try {
            value = objectWriter.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    protected void writeToFile(File file, String value) {
        try {
            FileUtils.writeStringToFile(file, value, DEFAULT_CHARSET);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
