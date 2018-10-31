package io.sitoolkit.cv.core.domain.report.designdoc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.infra.util.JsonUtils;

public class DesignDocReportProcessor {
    public List<Report> process(List<DesignDoc> designDocs) {
        List<Report> reports = processToDetailReports(designDocs);

        reports.add(processToIdListReport(designDocs));

        return reports;
    }

    private List<Report> processToDetailReports(List<DesignDoc> designDocs) {
        return designDocs.stream().collect(Collectors.groupingBy((d) -> getDetailPath(d)))
                .entrySet().stream().map((e) -> {
                    return processToDetailReport(e.getKey(), e.getValue());
                }).collect(Collectors.toList());
    }

    private Report processToDetailReport(String path, List<DesignDoc> designDocs) {
        String content = designDocs.stream().map(this::getDetailContent)
                .collect(Collectors.joining(";"));

        return new Report(Paths.get(path), content);
    }

    private Report processToIdListReport(List<DesignDoc> designDocs) {
        Path path = Paths.get("assets/designdoc-id-list.js");
        String content = getIdListContent(designDocs);

        return new Report(path, content);
    }

    private String getDetailPath(DesignDoc designDoc) {
        String dirName = designDoc.getPkg().replaceAll("\\.", "/");
        String fileName = designDoc.getClassName() + ".js";

        return dirName + "/" + fileName;
    }

    private String getDetailContent(DesignDoc designDoc) {
        DesignDocReportDetailDef detail = new DesignDocReportDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getComments().putAll(diagram.getComments());
        });
        return "window.reportData.designDoc.detailList['" + designDoc.getId() + "'] = "
                + JsonUtils.obj2str(detail);
    }

    private String getIdListContent(List<DesignDoc> designDocs) {
        Map<String, String> idList = new LinkedHashMap<>();
        designDocs.stream().forEach((designDoc) -> {
            idList.put(designDoc.getId(), getDetailPath(designDoc));
        });

        return "window.reportData.designDoc.idList = " + JsonUtils.obj2str(idList);
    }
}
