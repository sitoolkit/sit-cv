package io.sitoolkit.design.infra.config;

import org.springframework.context.annotation.Configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@Data
public class ApplicationConfig {

    private String project;
    private String allowedOrigins;
}
