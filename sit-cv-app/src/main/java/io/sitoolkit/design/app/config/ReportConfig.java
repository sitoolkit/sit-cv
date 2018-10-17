package io.sitoolkit.design.app.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.cv.core.domain.report.ReportWriter;

@Configuration
@ConditionalOnProperty( value = "report" )
public class ReportConfig {

    @Bean
    public ReportService reportService() {
        return new ReportService();
    }

    @Bean
    public ReportWriter reportWriter() {
        return new ReportWriter();
    }

}
