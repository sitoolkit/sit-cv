package io.sitoolkit.cv.core.domain.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.Deflater;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReportWriter {
    private static final String OUTPUT_DIR = "docs/designdocs";
    private static final String RESOURCE_NAME = "static";
    private Deflater compresser = new Deflater();

    public void write(List<DesignDoc> designDocs, String prjDirName) {
        try {
            File outputDir = new File(prjDirName, OUTPUT_DIR);
            FileUtils.deleteDirectory(outputDir);

            copyFromResource(RESOURCE_NAME, outputDir);
            writeDesignDocs(outputDir, designDocs);
            setReportConfig(outputDir);

            log.info("exported report to: {}", outputDir.toPath().toAbsolutePath().normalize());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void copyFromResource(String source, File target) {
        try {
            URL url = new URL(getClass().getClassLoader().getResource(source).toString());
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            if (connection instanceof JarURLConnection) {
                copyFromJarResource((JarURLConnection)connection, target);
            } else {
                FileUtils.copyDirectory(new File(url.getPath()), target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void copyFromJarResource(JarURLConnection connection, File target) {
        try {
            JarFile jarFile = connection.getJarFile();
            for(JarEntry entry : Collections.list(jarFile.entries())) {
                if(entry.getName().startsWith(connection.getEntryName())) {
                    String fileName = StringUtils.removeStart(entry.getName(), connection.getEntryName());
                    File targetFile = new File(target, fileName);

                    if (entry.isDirectory()) {
                        targetFile.mkdirs();
                    } else {
                        try (InputStream entryInputStream = jarFile.getInputStream(entry)) {
                            FileUtils.copyInputStreamToFile(entryInputStream, targetFile);
                        }
                    }
                }
            }
        } catch (IOException e) {
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
        String fileName = compressString(designDoc.getId()) + ".js";

        File detailDir = new File(outputDir, dirName);
        detailDir.mkdirs();

        File detailFile = new File(detailDir, fileName);
        String value = "window.reportData.designDoc.detailList['" +  designDoc.getId() + "'] = "
                + convertToJsonString(detail);
        writeToFile(detailFile, value);

        return dirName + "/" + fileName;
    }

    void writeDesignDocIdList(File outputDir, Map<String, String> idList) {
        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String idListValue = convertToJsonString(idList);
        writeToFile(idListFile, "window.reportData.designDoc.idList = " + idListValue);
    }

    String compressString(String input) {
        String encoded = null;
        try {
            byte[] dataByte = input.getBytes();

            compresser.reset();
            compresser.setLevel(Deflater.BEST_COMPRESSION);
            compresser.setInput(dataByte);
            compresser.finish();

            try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataByte.length)){
                byte[] buf = new byte[1024];
                while(!compresser.finished()) {
                    int compByte = compresser.deflate(buf);
                    byteArrayOutputStream.write(buf, 0, compByte);
                }
                byte[] compData = byteArrayOutputStream.toByteArray();
                encoded = Base64.getUrlEncoder().withoutPadding().encodeToString(compData);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encoded;
    }

    String convertToJsonString(Object src) {
        ObjectMapper mapper = new ObjectMapper();
        String value;
        try {
            value = mapper.writeValueAsString(src);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return value;
    }

    void writeToFile(File file, String value) {
        try {
            FileUtils.writeStringToFile(file, value, StandardCharsets.UTF_8);
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
