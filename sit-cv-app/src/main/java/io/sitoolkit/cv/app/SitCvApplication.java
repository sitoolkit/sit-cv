package io.sitoolkit.cv.app;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.sitoolkit.cv.core.app.config.ApplicationMode;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

@SpringBootApplication
public class SitCvApplication {

    public static void main(String[] args) {
        ApplicationArguments appArgs = new DefaultApplicationArguments(args);

        if (appArgs.containsOption("report")) {
            executeReportMode(appArgs);
        } else {
            SpringApplication.run(SitCvApplication.class, args);
        }
    }

    static void executeReportMode(ApplicationArguments appArgs) {
        List<String> projects = appArgs.getOptionValues("project");
        Path projectDir = projects == null || projects.isEmpty() ? Paths.get(".")
                : Paths.get(projects.get(0));
        ServiceFactory.initialize(projectDir, ApplicationMode.REPORT).getReportService().export();
    }
}
