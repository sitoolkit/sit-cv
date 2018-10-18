package io.sitoolkit.cv.core.domain.report.designdoc;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.report.ReportModel;
import io.sitoolkit.cv.core.infra.util.StrUtils;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@Builder
@EqualsAndHashCode(callSuper=true)
public class ReportDesignDoc extends ReportModel {
    private List<DesignDoc> designDocs;

    @Override
    public void write(File outputDir) {
        Map<String, String> idList = new HashMap<>();

        getDesignDocs().stream().forEach((designDoc) -> {
            String detailScriptPath = writeDetail(outputDir, designDoc);
            idList.put(designDoc.getId(), detailScriptPath);
        });

        writeIdList(outputDir, idList);
    }

    private String writeDetail(File outputDir, DesignDoc designDoc) {
        ReportDesignDocDetailDef detail = new ReportDesignDocDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getComments().put(diagram.getId(), diagram.getComments());
        });

        String dirName = designDoc.getPkg().replaceAll("\\.", "/");
        String fileName = StrUtils.compressAsFilename(designDoc.getId()) + ".js";

        File detailDir = new File(outputDir, dirName);
        detailDir.mkdirs();

        File detailFile = new File(detailDir, fileName);
        String value = "window.reportData.designDoc.detailList['" +  designDoc.getId() + "'] = "
                + convertObjectToJsonString(detail);
        writeToFile(detailFile, value);

        return dirName + "/" + fileName;
    }

    private void writeIdList(File outputDir, Map<String, String> idList) {
        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String value = "window.reportData.designDoc.idList = "
                + convertObjectToJsonString(idList);
        writeToFile(idListFile, value);
    }

}
