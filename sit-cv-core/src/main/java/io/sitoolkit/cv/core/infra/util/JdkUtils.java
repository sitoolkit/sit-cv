package io.sitoolkit.cv.core.infra.util;

import java.nio.file.Path;
import java.nio.file.Paths;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdkUtils {

    private JdkUtils() {
    }

    public static boolean isJdkToolsJarLoaded() {
        try {
            Class.forName("com.sun.tools.javac.tree.JCTree");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean loadJdkToosJar() {
        String javaHome = System.getProperty("java.home");
        Path toolsJar = Paths.get(javaHome, "/lib/tools.jar");

        if (!toolsJar.toFile().exists()) {
            toolsJar = Paths.get(javaHome, "../lib/tools.jar").normalize();
        }

        if (!toolsJar.toFile().exists()) {
            return false;
        }

        return ClassLoaderUtils.addPathToClasspath(toolsJar);
    }
}
