package io.sitoolkit.cv.app.infra.config;

import java.nio.file.Paths;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.cv.app.pres.designdoc.DesignDocMenuBuilder;
import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;

@Configuration
public class BaseConfig {

    @Bean
    @ConfigurationProperties(prefix = SitCvApplicationOption.PREFIX)
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig();
    }

    @Bean
    public ServiceFactory serviceFactory(ApplicationConfig appilcationConfig) {
        return ServiceFactory.create(Paths.get(appilcationConfig.getProject()));
    }

    @Bean
    public FunctionModelService functionModelService(ServiceFactory serviceFacotry) {
        return serviceFacotry.getFunctionModelService();

    }

    @Bean
    public ProjectManager projectManager(ServiceFactory serviceFactory) {
        return serviceFactory.getProjectManager();
    }

    @Bean
    public DesignDocMenuBuilder designDocMenuBuilder() {
        return new DesignDocMenuBuilder();
    }

    @Bean
    public CrudService crudService(ServiceFactory serviceFactory) {
        return serviceFactory.getCrudService();
    }
}
