package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.ResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
    private static final String OUTPUT_DIR = "docs/designdocs";
    private static final String RESOURCE_NAME = "static";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public void write(Path projectDir, List<Report> reports) {
        File outputDir = new File(projectDir.toString(), OUTPUT_DIR);
        Path outputDirPath = outputDir.toPath();

        try {
            FileUtils.deleteDirectory(outputDir);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ResourceUtils.copy(getClass(), RESOURCE_NAME, outputDir);
        setReportConfig(outputDirPath);

        writeReports(outputDirPath, reports);

        log.info("completed write to: {}",
                outputDir.toPath().toAbsolutePath().normalize());
    }

    void writeReports(Path outputDirPath, List<Report> reports) {
        reports.stream().forEach((report) -> {
            writeToFile(outputDirPath.resolve(report.getPath()).toFile(), report.getContent());
        });
    }

    void writeToFile(File file, String value) {
        try {
            FileUtils.writeStringToFile(file, value, DEFAULT_CHARSET);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

    void setReportConfig(Path outputDirPath) {
        try {
            Files.copy(
                outputDirPath.resolve("assets/config-report.js"),
                outputDirPath.resolve("assets/config.js"),
                StandardCopyOption.REPLACE_EXISTING
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
