package io.sitoolkit.cv.tools.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {
    }

    public static <T> T url2obj(URL url, Class<T> objType) {
        try {

            return MAPPER.readValue(extractNestedJar(url), objType);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * If a url points to a resource in a "nested jar file" like follows,
     * 
     * <pre>
     * jar:file:/path/to/sit-cv-app-1.0.0-beta.4-SNAPSHOT-exec.jar!/BOOT-INF/lib/sit-cv-core-1.0.0-beta.4-SNAPSHOT.jar!/io/sitoolkit/cv/core/infra/config/sit-cv-config.json
     * </pre>
     * 
     * extracts the outer jar file and returns the resource url in the extraced
     * jar file.
     * 
     * @param url
     *            resource url
     * @return resource url in the extraced jar
     * @throws IOException
     */
    static URL extractNestedJar(URL url) throws IOException {
        String urlStr = url.toString();
        if (StringUtils.countMatches(urlStr, "!") <= 1) {
            return url;
        }

        int first = urlStr.indexOf("!");
        int second = StringUtils.indexOf(urlStr, "!", first + 1);
        String outerJarUrlStr = urlStr.substring(0, second);
        String fileNameInOuterJarUrlStr = StringUtils.substringAfterLast(outerJarUrlStr, "/");

        Path filePath = Files.createTempFile("", fileNameInOuterJarUrlStr);
        filePath.toFile().deleteOnExit();

        Files.copy(new URL(outerJarUrlStr).openStream(), filePath,
                StandardCopyOption.REPLACE_EXISTING);

        String innerFilePart = urlStr.substring(second);
        String extractedFileUrlStr = filePath.toUri().toURL().toString();

        return extractNestedJar(new URL("jar:" + extractedFileUrlStr + innerFilePart));
    }
}
