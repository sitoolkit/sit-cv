package io.sitoolkit.cv.plugin.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.cv.core.app.config.ApplicationMode;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    @Parameter(property = "target.project", defaultValue = ".")
    private String[] projects;

    @Override
    public void execute() {
        Path projectDir = Paths.get(projects[0]);
        ServiceFactory.initialize(projectDir, ApplicationMode.REPORT).getReportService().export();
    }

}
