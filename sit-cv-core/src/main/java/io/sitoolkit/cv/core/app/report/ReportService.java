package io.sitoolkit.cv.core.app.report;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.ReportWriter;

public class ReportService {

    @Resource
    ReportWriter reportWriter;

    @Resource
    DesignDocService designDocService;

    public void write() {
        write("./");
    }

    public void write(String prjDirName) {
        Path prjDir = Paths.get(prjDirName);
        Path srcDir = prjDir.resolve("src/main/java");
        designDocService.loadDir(prjDir, srcDir);

        Set<String> designDocIds = designDocService.getAllIds();

        List<DesignDoc> designDocs = designDocIds.stream().map((designDocId) -> {
            return designDocService.get(designDocId);
        }).collect(Collectors.toList());

        reportWriter.write(designDocs, prjDirName);
    }

}
