package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
    private static final String OUTPUT_DIR = "docs/designdocs";
    private static final String RESOURCE_NAME = "static";

    public void write(List<ReportModel> models, String prjDirName) {
        File outputDir = new File(prjDirName, OUTPUT_DIR);

        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ResourceUtils.copy(getClass(), RESOURCE_NAME, outputDir);
        models.stream().forEach((m) -> m.write(outputDir));
        setReportConfig(outputDir);

        log.info("completed write to: {}",
                outputDir.toPath().toAbsolutePath().normalize());
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
