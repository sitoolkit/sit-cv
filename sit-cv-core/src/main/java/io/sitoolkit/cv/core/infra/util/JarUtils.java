package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarUtils {

    public static String getImplementationVersion(String file) {
        try (JarFile jarFile = new JarFile(file)) {
            Manifest manifest = jarFile.getManifest();
            return manifest.getMainAttributes().getValue("Implementation-Version");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
