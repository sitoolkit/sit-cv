package io.sitoolkit.cv.core.app.report;

import java.nio.file.Path;
import java.util.List;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
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

    private DesignDocService designDocService;

    private ProjectManager projectManager;

    public void export() {
        Path projectDir = projectManager.getCurrentProject().getDir();
        reportWriter.initDirectory(projectDir);

        List<DesignDoc> designDocs = designDocService.getAll();

        List<Report> reports = designDocReportProcessor.process(designDocs);

        reportWriter.write(projectDir, reports);
    }

}
