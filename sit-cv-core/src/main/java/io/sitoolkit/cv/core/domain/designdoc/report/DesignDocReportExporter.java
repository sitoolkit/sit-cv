package io.sitoolkit.cv.core.domain.designdoc.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilterConditionReader;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.domain.uml.plantuml.PlantUmlWriter;
import io.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.infra.config.Config;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

@Slf4j
public class DesignDocReportExporter {
    private final String jarList = "sit-cv-jar-list.txt";
    private final String srcDirName = "src/main/java";
    private final String outputDirName = "docs/designdocs";
    private final String resourceName = "report-resource";
    private PlantUmlWriter plantumlWriter = new PlantUmlWriter();
    private Deflater compresser = new Deflater();

    public void export() {
        export("./");
    }

    public void export(String prjDirName) {
        initGraphviz();

        ClassDefRepository repository = createClassDefRepository(prjDirName, srcDirName);

        try {
            File outputDir = new File(prjDirName, outputDirName);

            if(outputDir.exists()) {
                deleteDirectory(outputDir);
            }
            outputDir.mkdirs();

            copyResource(resourceName, outputDir);
            writeDesignDocs(outputDir, repository);
            Files.copy(
                new File(outputDir, "assets/config-report.js").toPath(),
                new File(outputDir, "assets/config.js").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );

            log.info("exported report to: " + outputDir.toPath());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    void initGraphviz () {
        if (SystemUtils.IS_OS_WINDOWS) {
            GraphvizManager graphvizManager = new GraphvizManager();
            String graphvizPath = graphvizManager.getBinaryPath().toAbsolutePath().toString();
            GraphvizUtils.setDotExecutable(graphvizPath);
        }
    }

    ClassDefRepository createClassDefRepository(String prjDir, String srcDir) {
        ClassDefFilter filter = new ClassDefFilter();
        ClassDefFilterConditionReader.read(Paths.get(prjDir)).ifPresent(filter::setCondition);

        ClassDefRepositoryParam param = new ClassDefRepositoryParam();
        param.setProjectDir(Paths.get(prjDir));
        param.setSrcDirs(Arrays.asList(Paths.get(srcDir)));
        param.setJarPaths(Arrays.asList());
        param.setJarList(Paths.get(prjDir, jarList));
        param.setBinDirs(Arrays.asList());

        ClassDefRepository repository = new ClassDefRepositoryMemImpl();
        Config config = new Config();

        ClassDefReader reader = new ClassDefReaderJavaParserImpl(repository, config);
        reader.init(param);
        reader.readDir(Paths.get(srcDir));

        return repository;
    }

    void writeDesignDocs(File outputDir, ClassDefRepository repository) {
        Map<String, String> idList = new HashMap<>();

        Set<String> designDocIds = repository.getEntryPoints();
        designDocIds.stream().forEach((designDocId) -> {
            MethodDef entryPoint = repository.findMethodByQualifiedSignature(designDocId);
            DesignDocReportDetail detail = createDetail(entryPoint);

            String detailDirName = createDetailDirName(entryPoint);
            String detailFileName = createDetailFileName(entryPoint);
            idList.put(designDocId, detailDirName + "/" + detailFileName);

            File detailDir = new File(outputDir, detailDirName);
            detailDir.mkdirs();

            File detailFile = new File(detailDir, detailFileName);
            String detailValue = convertToJsonString(detail);
            writeFile(detailFile, "window.reportData.designDoc.detailList['" + designDocId + "'] = " + detailValue);
        });

        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String idListValue = convertToJsonString(idList);
        writeFile(idListFile, "window.reportData.designDoc.idList = " + idListValue);
    }

    void deleteDirectory(File target) {
        Path targetPath = target.toPath();
        try(Stream<Path> walk = Files.walk(targetPath))
        {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void copyResource(String source, File target) {
        try {
            URL url = new URL(getClass().getClassLoader().getResource(source).toString());
            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);

            if (connection instanceof JarURLConnection) {
                copyJarResource((JarURLConnection)connection, target);
            } else {
                FileUtils.copyDirectory(new File(url.getPath()), target);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void copyJarResource(JarURLConnection connection, File target) {
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

    DesignDocReportDetail createDetail(MethodDef entryPoint) {
        DesignDocReportDetail detail = new DesignDocReportDetail();
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(getSequenceDiagram(entryPoint));
        diagrams.add(getClassDiagram(entryPoint));

        diagrams.stream().forEach((diagram) -> {
            detail.getDiagrams().put(diagram.getId(), new String(diagram.getData()));
            detail.getComments().put(diagram.getId(), diagram.getComments());
        });
        return detail;
    }

    Diagram getSequenceDiagram(MethodDef entryPoint) {
        SequenceDiagramWriterPlantUmlImpl writer = new SequenceDiagramWriterPlantUmlImpl();
        SequenceDiagramProcessor processor = new SequenceDiagramProcessor();
        LifeLineDef entryLifeLine = processor.process(entryPoint.getClassDef(), entryPoint);
        SequenceDiagram sd = SequenceDiagram.builder().entryLifeLine(entryLifeLine).build();
        return plantumlWriter.createDiagram(sd, writer::serialize);
    }

    Diagram getClassDiagram(MethodDef entryPoint) {
        ClassDiagramWriterPlantUmlImpl writer = new ClassDiagramWriterPlantUmlImpl();
        ClassDiagramProcessor processor = new ClassDiagramProcessor();
        ClassDiagram cd = processor.process(entryPoint);
        return plantumlWriter.createDiagram(cd, writer::serialize);
    }

    String createDetailFileName(MethodDef entryPoint) {
        ClassDef classDef = entryPoint.getClassDef();
        String className = classDef.getName();
        String methodName = entryPoint.getName();
        String argTypes = String.join("_", entryPoint.getParamTypes()
                .stream().map(TypeDef::getName).collect(Collectors.toList()));
        String uniqName = String.join("_", new String[] {className, methodName, argTypes});
        String fileName = compressString(uniqName) + ".js";
        return fileName;
    }

    String createDetailDirName(MethodDef entryPoint) {
        ClassDef classDef = entryPoint.getClassDef();
        return classDef.getPkg().replaceAll("\\.", "/");
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

    void writeFile(File file, String value) {
        try(FileWriter writer = new FileWriter(file);){
            writer.write(value);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
