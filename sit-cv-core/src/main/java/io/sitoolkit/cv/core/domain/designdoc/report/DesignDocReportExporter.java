package io.sitoolkit.cv.core.domain.designdoc.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.Deflater;

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

public class DesignDocReportExporter {

    private PlantUmlWriter plantumlWriter = new PlantUmlWriter();

    private Deflater compresser = new Deflater();

    public void export() {
        String prjDir = "../sample";
        String srcDir = "../sample/src/main/java";
        String jarList = "sit-cv-jar-list.txt";
        export(prjDir, srcDir, jarList);
    }

    public void export(String prjDir, String srcDir, String jarList) {
        String outputDirName = "docs/designdocs";

        initGraphviz();

        ClassDefRepository repository = createClassDefRepository(prjDir, srcDir, jarList);

        File outputDir = new File(prjDir, outputDirName);
        outputDir.mkdirs();

        try {
            deleteDirectory(outputDir.toString());

            copyResource("static", outputDir.toPath());
            writeDesignDocsData(outputDir, repository);
            Files.copy(
                new File(outputDir, "assets/config-report.js").toPath(),
                new File(outputDir, "assets/config.js").toPath(),
                StandardCopyOption.REPLACE_EXISTING
            );
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

    ClassDefRepository createClassDefRepository(String prjDir, String srcDir, String jarList) {
        ClassDefFilter filter = new ClassDefFilter();
        ClassDefFilterConditionReader.read(Paths.get(prjDir)).ifPresent(filter::setCondition);

        ClassDefRepositoryParam param = new ClassDefRepositoryParam();
        param.setProjectDir(Paths.get(prjDir));
        param.setSrcDirs(new ArrayList<>());
        param.getSrcDirs().add(Paths.get(srcDir));
        param.setJarPaths(new ArrayList<>());
        param.setJarList(Paths.get(jarList));
        param.setBinDirs(new ArrayList<>());
//        param.getBinDirs().add(Paths.get("target/classes"));

        ClassDefRepository repository = new ClassDefRepositoryMemImpl();
        Config config = new Config();
        config.setJarList(jarList);
        ClassDefReader reader = new ClassDefReaderJavaParserImpl(repository, config);
        reader.init(param);
        reader.readDir(Paths.get(srcDir));

        return repository;
    }

    void writeDesignDocsData(File outputDir, ClassDefRepository repository) {
        Map<String, String> idList = new HashMap<>();

        Set<String> designDocIds = repository.getEntryPoints();
        designDocIds.stream().forEach((designDocId) -> {
            MethodDef entryPoint = repository.findMethodByQualifiedSignature(designDocId);
            DesignDocReportDetailData detail = createDetailData(entryPoint);

            String detailDirName = createDetailDirName(entryPoint);
            String detailFileName = createDetailFileName(entryPoint);
            idList.put(designDocId, detailDirName + "/" + detailFileName);

            File detailDir = new File(outputDir, detailDirName);
            detailDir.mkdirs();

            File detailFile = new File(detailDir, detailFileName);
            String detailValue = toJsonString(detail);
            writeFile(detailFile, "window.reportData.designDoc.detailList['" + designDocId + "'] = " + detailValue);
        });

        File idListFile = new File(outputDir, "assets/designdoc-id-list.js");
        String idListValue = toJsonString(idList);
        writeFile(idListFile, "window.reportData.designDoc.idList = " + idListValue);
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

    void deleteDirectory(String target) {
        Path targetPath = Paths.get(target);
        try(Stream<Path> walk = Files.walk(targetPath))
        {
            walk.sorted(Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void copyResource(String source, Path target) {
        Path resourcePath = getResourcePath(source);

        try {
            Files.walkFileTree(resourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    Path targetDir = target.resolve(resourcePath.relativize(dir).toString());
                    Files.createDirectories(targetDir);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.copy(file, target.resolve(resourcePath.relativize(file).toString()), StandardCopyOption.REPLACE_EXISTING);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Path getResourcePath(String source) {
        Path path = null;

        try {
            URI resource = DesignDocReportExporter.class.getClassLoader().getResource(source).toURI();

            try {
                path = Paths.get(resource);
            } catch (FileSystemNotFoundException e) {
                Map<String, String> env = new HashMap<>();
                env.put("create", "true");
                FileSystem fileSystem = FileSystems.newFileSystem(resource, env);
                path = fileSystem.getPath(resource.toString());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return path;
    }

    String compressString(String input) {
        String encoded = null;
        try {
            byte[] dataByte = input.getBytes();

            compresser.reset();
            compresser.setLevel(Deflater.BEST_COMPRESSION);
            compresser.setInput(dataByte);
            compresser.finish();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(dataByte.length);
            byte[] buf = new byte[1024];
            while(!compresser.finished()) {
                int compByte = compresser.deflate(buf);
                byteArrayOutputStream.write(buf, 0, compByte);
            }
            byteArrayOutputStream.close();

            byte[] compData = byteArrayOutputStream.toByteArray();
            encoded = Base64.getEncoder().withoutPadding().encodeToString(compData);
        } catch(java.io.UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return encoded;
    }

    DesignDocReportDetailData createDetailData(MethodDef entryPoint) {
        DesignDocReportDetailData detail = new DesignDocReportDetailData();
        List<Diagram> diagrams = new ArrayList<>();
        diagrams.add(getSequenceDiagram(entryPoint));
        diagrams.add(getClassDiagram(entryPoint));

        diagrams.stream().forEach((diagram) -> {
            detail.getDiagrams().put(diagram.getId(), new String(diagram.getData()));
            detail.getComments().put(diagram.getId(), diagram.getComments());
        });
        return detail;
    }

    String createDetailFileName(MethodDef entryPoint) {
        ClassDef classDef = entryPoint.getClassDef();
        String className = classDef.getName();
        String name = entryPoint.getName();
        String argType = String.join("_", entryPoint.getParamTypes().stream().map(TypeDef::getName).collect(Collectors.toList()));
        String fileName = String.join("_", new String[] {className, argType, name});
        String fullFileName = compressString(fileName) + ".js";
        return fullFileName;
    }

    String createDetailDirName(MethodDef entryPoint) {
        ClassDef classDef = entryPoint.getClassDef();
        String pkgDir = classDef.getPkg().replaceAll("\\.", "/");
        return pkgDir;
    }

    String toJsonString(Object src) {
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
        try(FileWriter filewriter = new FileWriter(file);){
            filewriter.write(value);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
