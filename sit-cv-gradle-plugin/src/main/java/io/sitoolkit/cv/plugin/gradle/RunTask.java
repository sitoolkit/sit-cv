package io.sitoolkit.cv.plugin.gradle;

import org.apache.commons.lang3.StringUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import io.sitoolkit.cv.app.SitCvApplication;

public class RunTask extends DefaultTask {

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

    private String[] getCvArgsAsArray() {
        return StringUtils.isEmpty(cvArgs) ? new String[0] : cvArgs.split(" ");
    }

    @TaskAction
    public void run() {
        SitCvApplication.main(getCvArgsAsArray());
    }

}
