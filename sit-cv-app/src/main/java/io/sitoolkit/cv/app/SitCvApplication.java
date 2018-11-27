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

import io.sitoolkit.cv.core.app.config.ServiceFactory;

@SpringBootApplication
public class SitCvApplication {

    @Autowired
    private ApplicationContext appCtx;

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
        ServiceFactory.createAndInitialize(projectDir).getReportService().export();
    }

    @PostConstruct
    public void onBeforeContextRefresh() {
        appCtx.getBean(ServiceFactory.class).initialize();
    }

}
