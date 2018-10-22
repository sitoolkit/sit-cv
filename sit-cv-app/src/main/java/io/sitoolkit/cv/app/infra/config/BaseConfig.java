package io.sitoolkit.cv.app.infra.config;

import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;

@Configuration
public class BaseConfig {

    @Bean
    @ConfigurationProperties(prefix = "cv")
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig();
    }

    @Bean
    public ServiceFactory serviceFactory(ApplicationConfig appilcationConfig) {
        return ServiceFactory.initialize(Paths.get(appilcationConfig.getProject()));
    }

    @Bean
    public DesignDocService designService(ServiceFactory serviceFacotry) {
        return serviceFacotry.getDesignDocService();
    }

}
