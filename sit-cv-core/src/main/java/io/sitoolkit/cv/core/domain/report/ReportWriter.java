package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.io.FileUtils;

import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.cv.core.infra.util.SitResourceUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
  private static final String OUTPUT_DIR = "docs/designdocs";
  private static final String RESOURCE_NAME = "static";

  public void initDirectory(Path projectDir) {
    File outputDir = buildOutputDir(projectDir);

    try {
      FileUtils.deleteDirectory(outputDir);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
    SitResourceUtils.copy(getClass(), RESOURCE_NAME, outputDir);
  }

  public void write(Path projectDir, List<Report<?>> reports) {
    Path outputDirPath = buildOutputDir(projectDir).toPath();

    writeReports(outputDirPath, reports);

    log.info("completed write to: {}", outputDirPath.toAbsolutePath().normalize());
  }

  void writeReports(Path outputDirPath, List<Report<?>> reports) {
    reports
        .stream()
        .forEach(
            (report) -> {
              try {
                writeToFile(
                    outputDirPath.resolve(report.getPath()).toFile(), report2javascript(report));
              } catch (Exception e) {
                log.warn("Exception writing report: file '{}'", report.getPath(), e);
              }
            });
  }

  String report2javascript(Report<?> report) {
    return "postMessage(" + JsonUtils.obj2str(report) + ", '*');";
  }

  void writeToFile(File file, String value) {
    try {
      FileUtils.writeStringToFile(file, value, SitFileUtils.DEFAULT_CHARSET);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  File buildOutputDir(Path projectDir) {
    return new File(projectDir.toString(), OUTPUT_DIR);
  }
}
