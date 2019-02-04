package io.sitoolkit.cv.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.sitoolkit.cv.app.infra.config.SitCvApplicationOption;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

@SpringBootApplication
public class SitCvApplication {

    @Autowired
    private ApplicationContext appCtx;

    public static void main(String[] args) {
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        if (appArgs.containsOption(SitCvApplicationOption.REPORT.getKey())) {
            executeReportMode(appArgs);
        } else if (appArgs.containsOption(SitCvApplicationOption.ANALYZE_SQL.getKey())) {
            executeAnalyzeSqlMode(appArgs);
        } else {
            SpringApplication.run(SitCvApplication.class, args);
        }
    }

    private static Path getProjectDir(ApplicationArguments appArgs) {
        List<String> projects = appArgs.getOptionValues(SitCvApplicationOption.PROJECT.getKey());
        return projects == null || projects.isEmpty() ? Paths.get(".")
                : Paths.get(projects.get(0));
    }

    private static void executeReportMode(ApplicationArguments appArgs) {
        ServiceFactory.createAndInitialize(getProjectDir(appArgs)).getReportService().export();
    }

    private static void executeAnalyzeSqlMode(ApplicationArguments appArgs) {
        ServiceFactory.create(getProjectDir(appArgs)).getCrudService().analyzeSql();
    }

    /**
     * Initialize the service before the screen is reloaded by LiveReload of
     * spring-boot-devtools. The screen reload process is triggered when
     * ContextRefreshedEvent is notified.
     *
     * @see org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration
     */
    @PostConstruct
    public void initialize() {
        appCtx.getBean(ServiceFactory.class).initialize();
    }

}
