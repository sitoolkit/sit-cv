package io.sitoolkit.design.app.config;

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
    public ServiceFactory serviceFactory() {
        return ServiceFactory.initialize();
    }

    @Bean
    public DesignDocService designService(ServiceFactory serviceFacotry) {
        return serviceFacotry.getDesignDocService();
    }

}
