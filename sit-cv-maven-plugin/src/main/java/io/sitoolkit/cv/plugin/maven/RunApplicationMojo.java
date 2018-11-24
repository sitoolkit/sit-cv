package io.sitoolkit.cv.plugin.maven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.cv.app.SitCvApplication;

@Mojo(name = "run")
public class RunApplicationMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.basedir}")
    File projectDir;

    @Parameter(defaultValue = "x")
    String stopKey;

    @Override
    public void execute() throws MojoExecutionException {

        List<String> args = new ArrayList<>();
        if (projectDir != null) {
            args.add("--project");
            args.add(projectDir.getAbsolutePath());
        }
        SitCvApplication.main(args.toArray(new String[args.size()]));

        getLog().info("Press " + stopKey + " and enter to stop server");

        if (StringUtils.isNotEmpty(stopKey)) {
            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    if (StringUtils.equalsIgnoreCase(stopKey, scanner.nextLine())) {
                        break;
                    }
                }
            }
        }
    }

}
