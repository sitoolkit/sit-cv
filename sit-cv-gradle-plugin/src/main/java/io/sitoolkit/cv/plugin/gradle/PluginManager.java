package io.sitoolkit.cv.plugin.gradle;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

public class PluginManager implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().create("cvReport", ReportTask.class);
    }
}
