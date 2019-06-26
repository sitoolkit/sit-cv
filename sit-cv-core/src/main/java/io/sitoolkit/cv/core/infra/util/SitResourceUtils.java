package io.sitoolkit.cv.core.infra.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SitResourceUtils {

    private SitResourceUtils() {
    }

    public static void res2file(Object owner, String resourceName, Path targetPath) {
        res2file(owner.getClass(), resourceName, targetPath);
    }

    public static void res2file(Class<?> owner, String resourceName, Path targetPath) {
        URL resourceUrl = getResourceUrl(owner, resourceName);

        try {
            log.info("Write resource to {}", targetPath);
            FileUtils.copyURLToFile(resourceUrl, targetPath.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static String res2str(Object owner, String resourceName) {
        return res2str(owner.getClass(), resourceName);
    }

    public static String res2str(Class<?> owner, String resourceName) {
        URL resourceUrl = getResourceUrl(owner, resourceName);

        try {
            return IOUtils.toString(resourceUrl, Charset.defaultCharset());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static URL getResourceUrl(Class<?> owner, String resourceName) {
        URL resourceUrl = owner.getResource(resourceName);

        if (resourceUrl == null) {
            throw new IllegalArgumentException("resource not found:" + resourceName);
        }

        log.info("Read resource:{}", resourceUrl);

        return resourceUrl;
    }

    public static void copy(Class<?> clazz, String source, File target) {
        try {
            URL url = clazz.getClassLoader().getResource(source);
            if (url == null) {
                throw new FileNotFoundException("resource not found: " + source);
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            if (connection instanceof JarURLConnection) {
                copyFromJar((JarURLConnection) connection, target);
            } else {
                FileUtils.copyDirectory(new File(url.getPath()), target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void copyFromJar(JarURLConnection connection, File target) {
        try {
            JarFile jarFile = connection.getJarFile();
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                if (entry.getName().startsWith(connection.getEntryName())) {
                    String fileName = StringUtils.removeStart(entry.getName(),
                            connection.getEntryName());
                    File targetFile = new File(target, fileName);

                    if (entry.isDirectory()) {
                        targetFile.mkdirs();
                    } else {
                        try (InputStream entryInputStream = jarFile.getInputStream(entry)) {
                            FileUtils.copyInputStreamToFile(entryInputStream, targetFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
