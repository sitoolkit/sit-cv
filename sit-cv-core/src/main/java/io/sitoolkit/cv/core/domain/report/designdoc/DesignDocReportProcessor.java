package io.sitoolkit.cv.core.domain.report.designdoc;

import java.util.ArrayList;
import java.util.Collection;
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
        DetailReportsAndPathMap reportsAndPathMap = buildAndGroupingDetailReports(designDocs);
        reports.addAll(reportsAndPathMap.getReports());

        reports.add(buildDetailPathMapReport(reportsAndPathMap.getPathMap()));

        return reports;
    }

    private DetailReportsAndPathMap buildAndGroupingDetailReports(
            List<DesignDoc> designDocs) {
        DetailReportsAndPathMap reportsAndPathMap = new DetailReportsAndPathMap();

        designDocs.stream().forEach(designDoc -> {
            try {
                String path = buildDetailPath(designDoc);
                reportsAndPathMap.add(designDoc.getId(), path, buildDetail(designDoc));
            } catch (Exception e) {
                log.warn("Exception when build report: designDocId '{}'", designDoc.getId(), e);
            }
        });

        return reportsAndPathMap;
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
    class DetailReportsAndPathMap {
        /**
         * key:report.path
         */
        private Map<String, Report<DetailMap>> reportMap = new HashMap<>();
        /**
         * key:designDocId, value:report.path
         */
        private Map<String, String> pathMap = new LinkedHashMap<>();

        public void add(String designDocId, String path, DesignDocReportDetailDef detail) {
            pathMap.put(designDocId, path);
            Report<DetailMap> report = reportMap.computeIfAbsent(path,
                    p -> Report.<DetailMap>builder().path(p).content(new DetailMap()).build());
            report.getContent().getDetailMap().put(designDocId, detail);
        }

        public Collection<Report<DetailMap>> getReports() {
            return reportMap.values();
        }
    }

    @Data
    class DetailMap {
        /**
         * key:designDocId
         */
        private Map<String, DesignDocReportDetailDef> detailMap = new HashMap<>();
    }

}
