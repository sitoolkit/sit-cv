package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.infra.util.StrUtils;
import lombok.Builder;

@Builder
public class ReportDesignDoc implements ReportModel {
    List<DesignDoc> designDocs;

    @Override
    public void write(File outputDir, BiConsumer<File, String> writeToFile) {
        Map<String, String> idList = new HashMap<>();

        designDocs.stream().forEach((designDoc) -> {
            String detailScriptPath = writeDesignDocDetail(outputDir, designDoc, writeToFile);
            idList.put(designDoc.getId(), detailScriptPath);
        });

        writeDesignDocIdList(outputDir, idList, writeToFile);
    }

    private String writeDesignDocDetail(File outputDir, DesignDoc designDoc
            , BiConsumer<File, String> writeToFile) {
        ReportDesignDocDetailDef detail = new ReportDesignDocDetailDef();
        designDoc.getAllDiagrams().stream().forEach(diagram -> {
            String data = new String(diagram.getData());
            detail.getDiagrams().put(diagram.getId(), data);
            detail.getComments().put(diagram.getId(), diagram.getComments());
        });

        String dirName = designDoc.getPkg().replaceAll("\\.", "/");
        String fileName = StrUtils.compressFilename(designDoc.getId()) + ".js";

        File detailDir = new File(outputDir, dirName);
        detailDir.mkdirs();

        File detailFile = new File(detailDir, fileName);
        String value = "window.reportData.designDoc.detailList['" +  designDoc.getId() + "'] = "
                + StrUtils.convertToJsonString(detail);
        writeToFile.accept(detailFile, value);

        return dirName + "/" + fileName;
    }

    private void writeDesignDocIdList(File outputDir, Map<String, String> idList
            , BiConsumer<File, String> writeToFile) {
        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String value = "window.reportData.designDoc.idList = "
                + StrUtils.convertToJsonString(idList);
        writeToFile.accept(idListFile, value);
    }

}
