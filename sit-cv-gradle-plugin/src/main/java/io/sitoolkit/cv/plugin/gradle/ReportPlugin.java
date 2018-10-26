package io.sitoolkit.cv.plugin.gradle;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

import io.sitoolkit.cv.core.app.config.ServiceFactory;

public class ReportPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("cvReport", (task) -> {
            Path projectDir = Paths.get(System.getProperty("user.dir"));
            ServiceFactory.initialize(projectDir).getReportService().export();
        });
    }
}
