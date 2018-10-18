package io.sitoolkit.cv.core.domain.report;

import java.io.File;
import java.util.function.BiConsumer;

public interface ReportModel {

    void write(File outputDir, BiConsumer<File, String> writeToFile);

}
