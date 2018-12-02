package io.sitoolkit.cv.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import io.sitoolkit.cv.app.infra.config.SitCvApplicationOption;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

@SpringBootApplication
public class SitCvApplication {

    public static void main(String[] args) {
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        if (appArgs.containsOption(SitCvApplicationOption.REPORT.getKey())) {
            executeReportMode(appArgs);
        } else {
            ApplicationContext appCtx = SpringApplication.run(SitCvApplication.class, args);
            appCtx.getBean(ServiceFactory.class).initialize();
        }
    }

    private static void executeReportMode(ApplicationArguments appArgs) {
        List<String> projects = appArgs.getOptionValues(SitCvApplicationOption.PROJECT.getKey());
        Path projectDir = projects == null || projects.isEmpty() ? Paths.get(".")
                : Paths.get(projects.get(0));
        ServiceFactory.createAndInitialize(projectDir).getReportService().export();
    }
}
