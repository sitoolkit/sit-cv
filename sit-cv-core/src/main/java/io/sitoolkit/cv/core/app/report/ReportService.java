package io.sitoolkit.cv.core.app.report;

import java.util.List;

import io.sitoolkit.cv.core.app.function.FunctionModelService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.designdoc.DesignDocReportProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReportService {

    private DesignDocReportProcessor designDocReportProcessor;

    private ReportWriter reportWriter;

    private FunctionModelService functionModelService;

    private ProjectManager projectManager;

    public void export() {
        reportWriter.initDirectory(projectManager.getCurrentProject().getDir());

        List<DesignDoc> designDocs = functionModelService.getAll();

        List<Report<?>> reports = designDocReportProcessor.process(designDocs);

        reportWriter.write(projectManager.getCurrentProject().getDir(), reports);
    }

}
