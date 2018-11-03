package io.sitoolkit.cv.core.domain.report.designdoc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesignDocReportProcessor {

    public List<Report> process(List<DesignDoc> designDocs) {
        List<Report> reports = new ArrayList<>();
        DesignDocDetailReportsAndPathMap reportsAndPath = buildAndGroupingDetailReports(designDocs);
        reports.addAll(reportsAndPath.getReports());

        reports.add(buildIdListReport(reportsAndPath.getPathMap()));

        return reports;
    }

    private DesignDocDetailReportsAndPathMap buildAndGroupingDetailReports(List<DesignDoc> designDocs) {
        Map<String, Report> reportMap = new HashMap<>();
        DesignDocDetailReportsAndPathMap reportsAndPath = new DesignDocDetailReportsAndPathMap();

        designDocs.stream().forEach(designDoc -> {
            try {
                String path = buildDetailPath(designDoc);
                reportsAndPath.getPathMap().put(designDoc.getId(), path);

                Report report = reportMap.computeIfAbsent(path,
                        p -> Report.builder().path(Paths.get(p)).build());
                String detailContent = buildDetailContent(designDoc);
                report.setContent(report.getContent() + detailContent);
            } catch (Exception e) {
                log.warn("Exception when build report: designDocId '{}'", designDoc.getId(), e);
            }

        });

        reportsAndPath.getReports().addAll(reportMap.values());

        return reportsAndPath;
    }

    private String buildDetailPath(DesignDoc designDoc) {
        String dirName = designDoc.getPkg().replaceAll("\\.", "/");
        String fileName = designDoc.getClassName() + ".js";

        return dirName + "/" + fileName;
    }

    private String buildDetailContent(DesignDoc designDoc) {
        DesignDocReportDetailDef detail = new DesignDocReportDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getComments().putAll(diagram.getComments());
        });
        return "window.reportData.designDoc.detailList['" + designDoc.getId() + "'] = "
                + JsonUtils.obj2str(detail) + ";";
    }

    private Report buildIdListReport(Map<String, String> detailPathMap) {
        Path path = Paths.get("assets/designdoc-id-list.js");
        String content = "window.reportData.designDoc.idList = " + JsonUtils.obj2str(detailPathMap);

        return Report.builder().path(path).content(content).build();
    }

    @Data
    class DesignDocDetailReportsAndPathMap {
        private List<Report> reports = new ArrayList<>();
        private Map<String, String> pathMap = new LinkedHashMap<>();
    }
}
