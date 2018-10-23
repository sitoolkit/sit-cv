package io.sitoolkit.cv.app.infra.config;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Data
public class ApplicationConfig {

    private String project;
    private String allowedOrigins;

    public Path getProjectDir() {
        return Paths.get(project);
    }
}
