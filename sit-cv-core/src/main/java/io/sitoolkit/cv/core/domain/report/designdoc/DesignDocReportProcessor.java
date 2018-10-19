package io.sitoolkit.cv.core.domain.report.designdoc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.StrUtils;

public class DesignDocReportProcessor {
    public List<Report> process(List<DesignDoc> designDocs) {
        List<Report> reports = designDocs.stream()
                .map(this::processToDetailReport)
                .collect(Collectors.toList());

        reports.add(processToIdListReport(designDocs));

        return reports;
    }

    private Report processToDetailReport(DesignDoc designDoc) {
        DesignDocReportDetailDef detail = new DesignDocReportDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getComments().put(diagram.getId(), diagram.getComments());
        });

        Path path = Paths.get(getDetailPath(designDoc));
        String content = "window.reportData.designDoc.detailList['" +  designDoc.getId() + "'] = "
                + JsonUtils.convertObjectToString(detail);

        return new Report(path, content);
    }

    private Report processToIdListReport(List<DesignDoc> designDocs) {
        Map<String, String> idList = new HashMap<>();
        designDocs.stream().forEach((designDoc) -> {
            idList.put(designDoc.getId(), getDetailPath(designDoc));
        });

        Path path = Paths.get("assets/designdoc-id-list.js");
        String content = "window.reportData.designDoc.idList = "
                + JsonUtils.convertObjectToString(idList);

        return new Report(path, content);
    }

    private String getDetailPath(DesignDoc designDoc) {
        String dirName = designDoc.getPkg().replaceAll("\\.", "/");
        String fileName = StrUtils.compressAsFilename(designDoc.getId()) + ".js";

        return dirName + "/" + fileName;
    }

}
