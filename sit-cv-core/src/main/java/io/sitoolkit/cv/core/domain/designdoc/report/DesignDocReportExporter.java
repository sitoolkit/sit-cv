package io.sitoolkit.cv.core.domain.designdoc.report;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

public class DesignDocReportExporter {

    private PlantUmlWriter plantumlWriter = new PlantUmlWriter();

    private Deflater compresser = new Deflater();

    public void export() {
        String prjDir = "../sample";
        String srcDir = "../sample/src/main/java";
        String outputDir = "docs/designdocs";
        String jarList = "dummy";
        String filter = "filter";

        initGraphviz();

        ClassDefRepository repository = createClassDefRepository(jarList, filter, prjDir, srcDir);

        File dir = new File(prjDir, outputDir);
        dir.mkdirs();
        deleteDirectory(dir.toString());

        try {
            copyFromJar("static", dir.toPath());
            createDesignDocsData(dir, repository);
            Files.copy(
                    new File(dir, "assets/config-report.js").toPath(),
                    new File(dir, "assets/config.js").toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                    );
        } catch (Exception e) {
            new RuntimeException(e);
        }
    }

    void initGraphviz () {
        if (SystemUtils.IS_OS_WINDOWS) {
            GraphvizManager graphvizManager = new GraphvizManager();
            String graphvizPath = graphvizManager.getBinaryPath().toAbsolutePath().toString();
            GraphvizUtils.setDotExecutable(graphvizPath);
        }
    }

    ClassDefRepository createClassDefRepository(String jarList, String filter, String prjDir, String srcDir) {
        // ClassDefFilterConditionReader.read(prjDir).ifPresent(classFilter::setCondition);
        ClassDefRepository repository = new ClassDefRepositoryMemImpl();
        Config config = new Config();
        config.setJarList(jarList);
        ClassDefReader reader = new ClassDefReaderJavaParserImpl(repository, config);
        ClassDefRepositoryParam param = new ClassDefRepositoryParam();
        param.setSrcDirs(new ArrayList<>());
        param.setJarPaths(new ArrayList<>());
        param.setBinDirs(new ArrayList<>());
        // param.getBinDirs().add(Paths.get("target/classes"));
        param.setJarList(Paths.get(jarList));
        param.getSrcDirs().add(Paths.get(srcDir));
        param.setProjectDir(Paths.get(prjDir));
        reader.init(param);
        // reader.readDir(Paths.get("src/main/java"));
        reader.readDir(Paths.get(srcDir));

        return repository;
    }

    void createDesignDocsData(File dir, ClassDefRepository repository) throws JsonProcessingException {
        Map<String, String> idList = new HashMap<>();

        Set<String> designDocIds = repository.getEntryPoints();
        designDocIds.stream().forEach((designDocId) -> {
            MethodDef entryPoint = repository.findMethodByQualifiedSignature(designDocId);
            DesignDocReportDetailData detail = new DesignDocReportDetailData();
            List<Diagram> diagrams = new ArrayList<>();
            diagrams.add(getSequenceDiagram(entryPoint));
            diagrams.add(getClassDiagram(entryPoint));

            diagrams.stream().forEach((diagram) -> {
                detail.getDiagrams().put(diagram.getId(), new String(diagram.getData()));
                detail.getComments().put(diagram.getId(), diagram.getComments());
            });
            //data.getDesignDocDetailList().put(designDocId, detail);

            ClassDef classDef = entryPoint.getClassDef();
            String pkg = classDef.getPkg().replaceAll("\\.", "/");
            String className = classDef.getName();
            String name = entryPoint.getName();
            String argType = String.join("_", entryPoint.getParamTypes().stream().map(TypeDef::getName).collect(Collectors.toList()));
            String fileName = String.join("_", new String[] {className, argType, name});
            File targetDir = new File(dir, pkg);
            targetDir.mkdirs();

            String fullFileName = encodeString(fileName) + ".js";
            File file = new File(targetDir, fullFileName);
            idList.put(designDocId, pkg + "/" + fullFileName);

            try {
                String value = toJsonString(detail);
                outputFile(file, "window.designDocsData.detailList['" + designDocId + "'] = " + value);
            } catch (JsonProcessingException e) {
                new RuntimeException(e);
            }
        });

        File file = new File(dir, "assets/designdoc-id-list.js");
        String value = toJsonString(idList);
        outputFile(file, "window.reportData.designDoc.idList = " + value);
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

    void copyFromJar(String source, Path target) throws URISyntaxException, IOException {
        Path resourcePath = getResourcePath(source);

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
    }

    Path getResourcePath(String source) throws URISyntaxException, IOException {
        Path path = null;
        URI resource = getClass().getClassLoader().getResource(source).toURI();

        try {
            path = Paths.get(resource);
        } catch (FileSystemNotFoundException e) {
            Map<String, String> env = new HashMap<>();
            env.put("create", "true");
            FileSystem fileSystem = FileSystems.newFileSystem(resource, env);
            path = fileSystem.getPath(resource.toString());
        }

        return path;
    }

    String encodeString(String input) {
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

    String toJsonString(Object src) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String value = mapper.writeValueAsString(src);
        return value;
    }

    void outputFile(File file, String value) {
        try(FileWriter filewriter = new FileWriter(file);){
            filewriter.write(value);
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }

}
