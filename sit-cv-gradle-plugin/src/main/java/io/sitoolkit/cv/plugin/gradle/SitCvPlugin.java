package io.sitoolkit.cv.plugin.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class SitCvPlugin implements Plugin<Project> {
    private static final String GROUP_NAME = "SIT-CV";

    @Override
    public void apply(Project project) {
        project.getTasks().create("cvReport", ReportTask.class, (task) -> {
            task.setGroup(GROUP_NAME);
            task.setDescription("Export designdocs from source code");
        });
        project.getTasks().create("cvRun", RunTask.class, (task) -> {
            task.configure();
            task.setGroup(GROUP_NAME);
            task.setDescription("Run Code Visualizer server");
        });
    }
}
