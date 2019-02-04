package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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

    public static <T> T str2obj(String str, Object obj) {
        try {
            return mapper.readerForUpdating(obj).readValue(str);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static void obj2file(Object obj, Path path) {
        log.info("Write object to file: {}", formatPath(path));

        try {
            SitFileUtils.createDirectories(path.getParent());
            mapper.writeValue(path.toFile(), obj);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> Optional<T> file2obj(Path path, Class<T> objType) {
        if (!path.toFile().exists()) {
            log.info("File doesn't exist: {}", formatPath(path));
            return Optional.empty();
        }

        log.info("Read file to object: {}", formatPath(path));

        try {
            return Optional.of(mapper.readValue(path.toFile(), objType));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static <T> Optional<T> file2obj(Path path, TypeReference<T> objType) {
        if (!path.toFile().exists()) {
            log.info("File doesn't exist: {}", formatPath(path));
            return Optional.empty();
        }

        log.info("Read file to object: {}", formatPath(path));

        try {
            return Optional.of(mapper.readValue(path.toFile(), objType));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Path formatPath(Path path) {
        return path.toAbsolutePath().normalize();
    }

}
