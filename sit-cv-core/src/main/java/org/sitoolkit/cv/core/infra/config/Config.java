package org.sitoolkit.cv.core.infra.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class Config {
    private String jarList = "jar-list.txt";
    private String javaFilePattern = ".*\\.(java|class)$";
    private String srcDirs = "";
    private Properties prop = new Properties();
    private Map<String, String> replaceMap;

    static void load() {
        Path configFile = Paths.get("runtime.properties");
        if (!configFile.toFile().exists()) {
            return;
        }
        log.info("Config {}", configFile.toAbsolutePath());
        // try {
        // instance.prop.load(Files.newInputStream(configFile));
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }

    }

    public static void main(String[] args) {
        System.out.println("hoge.class".matches(".*\\.(java|class)$"));
    }
}
