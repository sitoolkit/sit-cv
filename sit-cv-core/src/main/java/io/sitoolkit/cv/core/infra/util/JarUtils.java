package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarUtils {

    public static String getImplementationVersion(String file) {
        return getManifest(file).getMainAttributes().getValue("Implementation-Version");
    }

    public static Manifest getManifest(String file) {
        try (JarFile jarFile = new JarFile(file)) {
            return jarFile.getManifest();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
