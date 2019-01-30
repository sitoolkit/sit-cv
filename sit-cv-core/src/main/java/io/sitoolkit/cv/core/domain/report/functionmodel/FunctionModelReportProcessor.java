package io.sitoolkit.cv.core.domain.report.functionmodel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.cv.core.domain.functionmodel.FunctionModel;
import io.sitoolkit.cv.core.domain.report.Report;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FunctionModelReportProcessor {

    private static final String BASE_DIR = "functionmodel/";

    public List<Report<?>> process(List<FunctionModel> functionModels) {
        List<Report<?>> reports = new ArrayList<>();
        DetailReportsAndPathMap reportsAndPathMap = buildAndGroupingDetailReports(functionModels);
        reports.addAll(reportsAndPathMap.getReports());

        reports.add(buildDetailPathMapReport(reportsAndPathMap.getPathMap()));

        return reports;
    }

    private DetailReportsAndPathMap buildAndGroupingDetailReports(
            List<FunctionModel> functionModels) {
        DetailReportsAndPathMap reportsAndPathMap = new DetailReportsAndPathMap();

        functionModels.stream().forEach(functionModel -> {
            try {
                String path = buildDetailPath(functionModel);
                reportsAndPathMap.add(functionModel.getId(), path, buildDetail(functionModel));
            } catch (Exception e) {
                log.warn("Exception when build report: functionId '{}'", functionModel.getId(), e);
            }
        });

        return reportsAndPathMap;
    }

    private String buildDetailPath(FunctionModel functionModel) {
        String dirName = functionModel.getPkg().replaceAll("\\.", "/");
        String fileName = functionModel.getClassName() + ".js";

        return BASE_DIR + dirName + "/" + fileName;
    }

    private FunctionModelReportDetailDef buildDetail(FunctionModel functionModel) {
        FunctionModelReportDetailDef detail = new FunctionModelReportDetailDef();
        functionModel.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getApiDocs().putAll(diagram.getApiDocs());
        });
        return detail;
    }

    private Report<Map<String, String>> buildDetailPathMapReport(
            Map<String, String> detailPathMap) {
        return Report.<Map<String, String>>builder().path(BASE_DIR + "detail-path-map.js")
                .content(detailPathMap).build();
    }

    @Data
    class DetailReportsAndPathMap {
        /**
         * key:report.path
         */
        private Map<String, Report<DetailMap>> reportMap = new HashMap<>();
        /**
         * key:functionId, value:report.path
         */
        private Map<String, String> pathMap = new LinkedHashMap<>();

        public void add(String functionId, String path, FunctionModelReportDetailDef detail) {
            pathMap.put(functionId, path);
            Report<DetailMap> report = reportMap.computeIfAbsent(path,
                    p -> Report.<DetailMap>builder().path(p).content(new DetailMap()).build());
            report.getContent().getDetailMap().put(functionId, detail);
        }

        public Collection<Report<DetailMap>> getReports() {
            return reportMap.values();
        }
    }

    @Data
    class DetailMap {
        /**
         * key:functionId
         */
        private Map<String, FunctionModelReportDetailDef> detailMap = new HashMap<>();
    }

}
