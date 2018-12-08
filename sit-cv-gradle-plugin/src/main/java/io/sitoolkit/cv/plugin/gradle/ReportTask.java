package io.sitoolkit.cv.plugin.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import io.sitoolkit.cv.core.app.config.ServiceFactory;

public class ReportTask extends DefaultTask {

    @TaskAction
    public void export() {
        ServiceFactory.createAndInitialize(getProject().getProjectDir().toPath()).getReportService()
                .export();
    }
}
