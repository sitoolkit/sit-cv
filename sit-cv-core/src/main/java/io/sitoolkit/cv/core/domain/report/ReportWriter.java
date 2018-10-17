package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.infra.util.StrUtils;
import io.sitoolkit.cv.core.infra.util.FileIOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
    private static final String OUTPUT_DIR = "docs/designdocs";
    private static final String RESOURCE_NAME = "static";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public void write(List<DesignDoc> designDocs, String prjDirName) {
        try {
            File outputDir = new File(prjDirName, OUTPUT_DIR);
            FileUtils.deleteDirectory(outputDir);

            FileIOUtils.copyFromResource(getClass(), RESOURCE_NAME, outputDir);
            writeDesignDocs(outputDir, designDocs);
            setReportConfig(outputDir);

            log.info("completed write to: {}", outputDir.toPath().toAbsolutePath().normalize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void writeDesignDocs(File outputDir, List<DesignDoc> designDocs) {
        Map<String, String> idList = new HashMap<>();

        designDocs.stream().forEach((designDoc) -> {
            String detailScriptPath = writeDesignDocDetail(outputDir, designDoc);
            idList.put(designDoc.getId(), detailScriptPath);
        });

        writeDesignDocIdList(outputDir, idList);
    }

    String writeDesignDocDetail(File outputDir, DesignDoc designDoc) {
        ReportDetailDef detail = new ReportDetailDef();
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
        writeToFile(detailFile, value);

        return dirName + "/" + fileName;
    }

    void writeDesignDocIdList(File outputDir, Map<String, String> idList) {
        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String value = "window.reportData.designDoc.idList = "
                + StrUtils.convertToJsonString(idList);
        writeToFile(idListFile, value);
    }

    void writeToFile(File file, String value) {
        try {
            FileUtils.writeStringToFile(file, value, DEFAULT_CHARSET);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    void setReportConfig(File outputDir) {
        try {
            Files.copy(
                new File(outputDir, "assets/config-report.js").toPath(),
                new File(outputDir, "assets/config.js").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
