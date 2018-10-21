package io.sitoolkit.design.app.config;

import java.nio.file.Paths;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.design.ApplicationConfig;

@Configuration
public class BaseConfig {

    @Bean
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig();
    }

    @Bean
    public ServiceFactory serviceFactory(ApplicationConfig appilcationConfig) {
        return ServiceFactory.initialize(Paths.get(appilcationConfig.getTargetProjectPath()));
    }

    @Bean
    public DesignDocService designService(ServiceFactory serviceFacotry) {
        return serviceFacotry.getDesignDocService();
    }

}