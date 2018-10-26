package io.sitoolkit.cv.plugin.gradle;

import  org.gradle.api.tasks.TaskAction;

import io.sitoolkit.cv.core.app.config.ServiceFactory;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.api.DefaultTask;

public class ReportTask extends DefaultTask {
    @TaskAction
    void export() {
        Path projectDir = Paths.get(System.getProperty("user.dir"));
        ServiceFactory.initialize(projectDir).getReportService().export();
    }
}
