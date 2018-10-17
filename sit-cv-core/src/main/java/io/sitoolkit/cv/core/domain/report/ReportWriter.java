package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.FileIOUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
    private static final String OUTPUT_DIR = "docs/designdocs";
    private static final String RESOURCE_NAME = "static";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public void write(List<ReportModel> models, String prjDirName) {
        try {
            File outputDir = new File(prjDirName, OUTPUT_DIR);
            FileUtils.deleteDirectory(outputDir);

            FileIOUtils.copyFromResource(getClass(), RESOURCE_NAME, outputDir);
            models.stream().forEach((m) -> m.write(outputDir, this::writeToFile));
            setReportConfig(outputDir);

            log.info("completed write to: {}",
                    outputDir.toPath().toAbsolutePath().normalize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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
