package io.sitoolkit.cv.plugin.gradle;

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

    private String stopKey = "x";

    private String cvArgs;

    @Input
    @Optional
    public String getCvArgs() {
        return cvArgs;
    }

    @Option(option = "cvArgs", description = "Project directory path to generate report")
    public void setCvArgs(String cvArgs) {
        this.cvArgs = cvArgs;
    }

    private List<String> getCvArgsAsList() {
        return StringUtils.isEmpty(cvArgs) ? Collections.emptyList()
                : Arrays.asList(cvArgs.split(" "));
    }

    public void configure() {
        setMain("io.sitoolkit.cv.app.SitCvApplication");
        FileCollection classpath = getProject().getBuildscript().getConfigurations()
                .findByName("classpath");
        setClasspath(classpath);
        setArgs(getCvArgsAsList());
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
