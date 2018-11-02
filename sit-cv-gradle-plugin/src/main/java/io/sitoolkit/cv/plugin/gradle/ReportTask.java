package io.sitoolkit.cv.plugin.gradle;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import io.sitoolkit.cv.core.app.config.ApplicationType;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

public class ReportTask extends DefaultTask {
    private Path basePath = getProject().getProjectDir().toPath();

    @Input
    private List<Path> projectDirs;

    @Option(option = "project", description = "Directories of target projects.")
    void setProjectDir(final List<String> dirs) {
        this.projectDirs = dirs.stream().map((dir) -> {
            return basePath.resolve(dir).normalize();
        }).collect(Collectors.toList());
    }

    @TaskAction
    void export() {
        if(projectDirs == null) {
            projectDirs = Arrays.asList(basePath);
        }

        ServiceFactory.initialize(projectDirs.get(0), ApplicationType.REPORT).getReportService().export();
    }
}
