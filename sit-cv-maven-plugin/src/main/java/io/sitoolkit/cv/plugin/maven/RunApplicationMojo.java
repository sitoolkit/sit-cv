package io.sitoolkit.cv.plugin.maven;

import java.util.ArrayList;
import java.util.Arrays;
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

    public static final String ANALYZE_SQL_OPTION = "analyze-sql";

    private static final String OPEN_BROWSER_OPTION = "open";
    
    @Parameter(property = ANALYZE_SQL_OPTION, defaultValue = "false")
    private boolean analyzeSql;

    @Parameter(property = OPEN_BROWSER_OPTION, defaultValue = "true")
    private boolean openBrowser;
    
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
        String[] cvArgsArray = StringUtils.defaultString(cvArgs).split(" ");
        List<String> args = new ArrayList<>(Arrays.asList(cvArgsArray));

        if(analyzeSql) {
          args.add("--cv." + ANALYZE_SQL_OPTION);
        }

        if(!openBrowser) {
          args.add("--cv." + OPEN_BROWSER_OPTION + "=false");
        }

        return args.toArray(new String[] {});
    }

}
