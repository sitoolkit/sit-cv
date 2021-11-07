package io.sitoolkit.cv.app.infra.config;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BaseConfig {

  @Bean
  @ConfigurationProperties(prefix = SitCvApplicationOption.PREFIX)
  public ApplicationConfig applicationConfig() {
    return new ApplicationConfig();
  }

  @Bean
  public ServiceFactory serviceFactory(ApplicationConfig appilcationConfig) {
    return ServiceFactory.create(appilcationConfig.getProjectDir(), true);
  }

  @Bean
  public FunctionModelService functionModelService(ServiceFactory serviceFacotry) {
    return serviceFacotry.getFunctionModelService();
  }

  @Bean
  public DesignDocService designDocService(ServiceFactory serviceFacotry) {
    return serviceFacotry.getDesignDocService();
  }

  @Bean
  public ProjectManager projectManager(ServiceFactory serviceFactory) {
    return serviceFactory.getProjectManager();
  }

  @Bean
  public CrudService crudService(ServiceFactory serviceFactory) {
    return serviceFactory.getCrudService();
  }
}
