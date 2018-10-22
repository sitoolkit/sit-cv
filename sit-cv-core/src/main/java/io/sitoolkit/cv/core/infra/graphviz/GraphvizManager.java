package io.sitoolkit.cv.core.infra.graphviz;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.zeroturnaround.zip.ZipUtil;

import io.sitoolkit.cv.core.infra.SitRepository;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

@Slf4j
public class GraphvizManager {
    private static String winGraphvizDownloadUrl;
    private static String winGraphvizInstallFile;

    private GraphvizManager() {
    }

    public static void initialize() {
        if (SystemUtils.IS_OS_WINDOWS) {
            loadProperties();
            checkBinary();
            GraphvizUtils.setDotExecutable(getBinaryPath().toAbsolutePath().toString());
        }
    }

    private static Path getGraphvizPath() {
        return SitRepository.getRepositoryPath().resolve("graphviz");
    };

    private static Path getBinaryPath() {
        return getGraphvizPath().resolve("release/bin/dot.exe");
    }

    private static void loadProperties() {
        ResourceBundle rb = ResourceBundle.getBundle("graphviz");
        winGraphvizDownloadUrl = rb.getString("win.graphviz.downloadUrl");
        winGraphvizInstallFile = rb.getString("win.graphviz.installFile");
    }

    private static void checkBinary() {
        Path binaryPath = getBinaryPath();
        if (Files.exists(binaryPath)) {
            log.info("Executable Graphviz found in SitRepository : {}", binaryPath);

        } else {
            log.info("Graphviz not found in SitRepository");
            installGraphviz();
        }
    }

    private static void installGraphviz() {
        prepareDirectory();
        installGraphvizWindows();
    }

    private static void installGraphvizWindows() {
        log.info("Installing Graphviz...");
        downloadWindowsBinary();
        extractBinary();
        log.info("Finished Installing Graphviz");
    }

    private static void prepareDirectory() {
        if (!Files.exists(getGraphvizPath())) {
            try {
                Files.createDirectories(getGraphvizPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void downloadWindowsBinary() {
        Path zipFile = getGraphvizPath().resolve(winGraphvizInstallFile);

        if (Files.exists(zipFile)) {
            log.info("zipFile exists :{}", zipFile.toString());
        } else {
            // TODO proxy対応
            try {
                URL url = new URL(winGraphvizDownloadUrl);
                log.info("downloading zipFile from '{}' ... ", url);
                FileUtils.copyURLToFile(url, zipFile.toFile());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static void extractBinary() {
        Path zipFile = getGraphvizPath().resolve(winGraphvizInstallFile);
        log.info("extracting zipfile '{}' ... ", zipFile);
        ZipUtil.unpack(zipFile.toFile(), getGraphvizPath().toFile());
        if (!Files.exists(getBinaryPath())) {
            log.error("graphviz executable not found at '{}' ", getBinaryPath().toAbsolutePath());
            throw new IllegalStateException(
                    "graphviz executable not found at " + getBinaryPath().toAbsolutePath());
        }
    }

}
