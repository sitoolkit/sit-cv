package io.sitoolkit.cv.core.app.report;

import java.util.List;

import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.function.FunctionModel;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.function.FunctionModelReportProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReportService {

    private FunctionModelReportProcessor functionModelReportProcessor;

    private ReportWriter reportWriter;

    private FunctionModelService functionModelService;

    private ProjectManager projectManager;

    public void export() {
        reportWriter.initDirectory(projectManager.getCurrentProject().getDir());

        List<FunctionModel> functionModels = functionModelService.getAll();

        List<Report<?>> reports = functionModelReportProcessor.process(functionModels);

        reportWriter.write(projectManager.getCurrentProject().getDir(), reports);
    }

}
