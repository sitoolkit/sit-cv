package io.sitoolkit.cv.plugin.gradle;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.options.Option;

public class RunTask extends JavaExec {

    public static final String ANALYZE_SQL_OPTION = "analyze-sql";

    public static final String ANALYZE_SQL_DESCRIPTION = "Run tests and analyze SQL logs to generate a CRUD matrix";

    private static final String OPEN_BROWSER_OPTION = "open";
    
    private String stopKey = "x";

    private String cvArgs;

    private boolean analyzeSql;

    private String openBrowser;
    
    @Input
    @Optional
    public String getCvArgs() {
        return cvArgs;
    }

    @Option(option = "cvArgs", description = "Project directory path to generate report")
    public void setCvArgs(String cvArgs) {
        this.cvArgs = cvArgs;
    }

    @Option(option = ANALYZE_SQL_OPTION, description = ANALYZE_SQL_DESCRIPTION)
    public void setAnalyzeSql(boolean analyzeSql) {
        this.analyzeSql = analyzeSql;
    }

    @Option(option = OPEN_BROWSER_OPTION, description = "Set false to not open the browser at startup")
    public void setOpenBrowser(String openBrowser) {
        this.openBrowser = openBrowser;
    }
    
    private List<String> getCvArgsAsList() {
        List<String> args = new ArrayList<>();

        if (StringUtils.isNotEmpty(cvArgs)) {
            args.addAll(Arrays.asList(cvArgs.split(" ")));
        }

        if (analyzeSql) {
            args.add("--cv." + ANALYZE_SQL_OPTION);
        }

        if (!StringUtils.isEmpty(openBrowser) && openBrowser.equals("false")) {
          args.add("--cv." + OPEN_BROWSER_OPTION + "=false");
        }

        return args;
    }

    @Override
    public void exec() {
        setArgs(getCvArgsAsList());
        super.exec();
    }

    public void configure() {
        setMain("io.sitoolkit.cv.app.SitCvApplication");
        FileCollection classpath = getProject().getBuildscript().getConfigurations()
                .findByName("classpath");
        setClasspath(classpath);
        getConventionMapping().map("jvmArgs", new Callable<Iterable<String>>() {
            @SuppressWarnings("unchecked")
            @Override
            public Iterable<String> call() throws Exception {
                if (getProject().hasProperty("applicationDefaultJvmArgs")) {
                    return (Iterable<String>) getProject().property("applicationDefaultJvmArgs");
                }
                return Collections.emptyList();
            }
        });
    }

}
