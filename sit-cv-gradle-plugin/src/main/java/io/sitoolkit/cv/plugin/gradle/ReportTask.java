package io.sitoolkit.cv.plugin.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.options.Option;

import io.sitoolkit.cv.core.app.config.ServiceFactory;

public class ReportTask extends DefaultTask {

    private boolean analyzeSql;

    @Option(option = RunTask.ANALYZE_SQL_OPTION, description = RunTask.ANALYZE_SQL_DESCRIPTION)
    public void setAnalyzeSql(boolean analyzeSql) {
        this.analyzeSql = analyzeSql;
    }

    @TaskAction
    public void export() {
        ServiceFactory factory = ServiceFactory
                .createAndInitialize(getProject().getProjectDir().toPath());

        if (analyzeSql) {
            factory.getCrudService().analyzeSql();
        }

        factory.getReportService().export();
    }
}
