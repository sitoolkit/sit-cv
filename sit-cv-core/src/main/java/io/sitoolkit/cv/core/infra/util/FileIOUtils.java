package io.sitoolkit.cv.core.infra.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buidtoolhelper.UnExpectedException;

public class FileIOUtils {

    public static void copyFromResource(Class<?> clazz, String source, File target) {
        try {
            URL url = clazz.getClassLoader().getResource(source);
            if (url == null) {
                throw new FileNotFoundException("resource not found: " + source);
            }
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            if (connection instanceof JarURLConnection) {
                copyFromJarResource((JarURLConnection)connection, target);
            } else {
                FileUtils.copyDirectory(new File(url.getPath()), target);
            }
        } catch (IOException e) {
            throw new UnExpectedException(e);
        }
    }

    private static void copyFromJarResource(JarURLConnection connection, File target) {
        try {
            JarFile jarFile = connection.getJarFile();
            for(JarEntry entry : Collections.list(jarFile.entries())) {
                if(entry.getName().startsWith(connection.getEntryName())) {
                    String fileName = StringUtils.removeStart(entry.getName(), connection.getEntryName());
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
            throw new UnExpectedException(e);
        }
    }

}
