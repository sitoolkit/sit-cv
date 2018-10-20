package io.sitoolkit.design;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.env.StandardEnvironment;

import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.design.app.config.BaseConfig;
import io.sitoolkit.design.app.config.ReportConfig;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        if(appArgs.containsOption("report")) {
            try (AnnotationConfigApplicationContext appCtx = getApplicationContext(
                    appArgs, BaseConfig.class, ReportConfig.class)) {
                ReportService reportService = appCtx.getBean(ReportService.class);
                reportService.export();
            }
        } else {
            SpringApplication.run(Application.class, args);
        }
    }

    static AnnotationConfigApplicationContext getApplicationContext(
            ApplicationArguments appArgs, Class<?>... annotatedClasses) {
        AnnotationConfigApplicationContext appCtx = new AnnotationConfigApplicationContext();
        appCtx.getBeanFactory().registerSingleton("springApplicationArguments", appArgs);
        ConfigurableEnvironment env = new StandardEnvironment();
        env.getPropertySources().addFirst(
                new SimpleCommandLinePropertySource(appArgs.getSourceArgs()));
        appCtx.setEnvironment(env);
        appCtx.register(annotatedClasses);
        appCtx.refresh();
        return appCtx;
    }

}
