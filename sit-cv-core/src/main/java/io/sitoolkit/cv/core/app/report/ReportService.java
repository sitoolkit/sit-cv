package io.sitoolkit.cv.core.app.report;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.annotation.Resource;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.designdoc.DesignDocReportProcessor;

public class ReportService {

    @Resource
    DesignDocReportProcessor designDocReportProcessor;

    @Resource
    ReportWriter reportWriter;

    @Resource
    DesignDocService designDocService;

    public void write() {
        write(Paths.get("./"));
    }

    public void write(Path projectDir) {
        List<DesignDoc> designDocs = designDocService.loadDesignDocs(projectDir);

        List<Report> reports = designDocReportProcessor.process(designDocs);

        reportWriter.write(projectDir, reports);
    }

}
