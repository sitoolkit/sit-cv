package io.sitoolkit.cv.core.domain.report.designdoc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.Report;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesignDocReportProcessor {

    public List<Report<?>> process(List<DesignDoc> designDocs) {
        List<Report<?>> reports = new ArrayList<>();
        DesignDocDetailReportsAndPathMap reportsAndPath = buildAndGroupingDetailReports(designDocs);
        reports.addAll(reportsAndPath.getReports());

        reports.add(buildDetailPathMapReport(reportsAndPath.getPathMap()));

        return reports;
    }

    private DesignDocDetailReportsAndPathMap buildAndGroupingDetailReports(
            List<DesignDoc> designDocs) {
        Map<String, Report<Map<String, DesignDocReportDetailDef>>> reportMap = new HashMap<>();
        DesignDocDetailReportsAndPathMap reportsAndPath = new DesignDocDetailReportsAndPathMap();

        designDocs.stream().forEach(designDoc -> {
            try {
                String path = buildDetailPath(designDoc);
                reportsAndPath.getPathMap().put(designDoc.getId(), path);

                Report<Map<String, DesignDocReportDetailDef>> report = reportMap.computeIfAbsent(path,
                        p -> Report.<Map<String, DesignDocReportDetailDef>>builder().path(p)
                                .content(new HashMap<String, DesignDocReportDetailDef>()).build());
                report.getContent().put(designDoc.getId(), buildDetail(designDoc));
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

    private DesignDocReportDetailDef buildDetail(DesignDoc designDoc) {
        DesignDocReportDetailDef detail = new DesignDocReportDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getApiDocs().putAll(diagram.getApiDocs());
        });
        return detail;
    }

    private Report<Map<String, String>> buildDetailPathMapReport(
            Map<String, String> detailPathMap) {
        return Report.<Map<String, String>>builder().path("assets/designdoc-detail-path-map.js")
                .content(detailPathMap).build();
    }

    @Data
    class DesignDocDetailReportsAndPathMap {
        private List<Report<?>> reports = new ArrayList<>();
        private Map<String, String> pathMap = new LinkedHashMap<>();
    }
}
