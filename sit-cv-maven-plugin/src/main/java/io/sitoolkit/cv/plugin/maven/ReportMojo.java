package io.sitoolkit.cv.plugin.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import io.sitoolkit.cv.core.app.config.ServiceFactory;

@Mojo(name = "report")
public class ReportMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Override
    public void execute() {
        ServiceFactory.createAndInitialize(project.getBasedir().toPath()).getReportService()
                .export();
    }

}
