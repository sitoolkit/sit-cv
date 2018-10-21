package io.sitoolkit.design.infra.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.cv.core.app.config.ServiceFactory;
import io.sitoolkit.cv.core.app.report.ReportService;

@Configuration
@ConditionalOnProperty(value = "report")
public class ReportConfig {

    @Bean
    public ReportService reportService(ServiceFactory serviceFactory) {
        return serviceFactory.getReportService();
    }

}
