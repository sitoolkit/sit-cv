package io.sitoolkit.cv.plugin.maven;

import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.cv.app.SitCvApplication;

@Mojo(name = "run")
public class RunApplicationMojo extends AbstractMojo {

    public static final String ANALYZE_SQL_OPTION = "analyze-sql";
    
    @Parameter(property = ANALYZE_SQL_OPTION, defaultValue = "false")
    private boolean analyzeSql;
    
    @Parameter
    private String cvArgs;

    @Parameter(defaultValue = "x")
    private String stopKey;

    @Override
    public void execute() throws MojoExecutionException {

        SitCvApplication.main(getArgsAsArray());

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

    private String[] getArgsAsArray() {
        String args = StringUtils.defaultString(cvArgs);
        String analyzeSqlOption = analyzeSql ? (" --cv." + ANALYZE_SQL_OPTION) : "";

        return (args + analyzeSqlOption).trim().split(" ");
    }

}
