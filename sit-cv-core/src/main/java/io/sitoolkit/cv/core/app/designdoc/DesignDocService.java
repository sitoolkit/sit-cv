package io.sitoolkit.cv.core.app.designdoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramModel;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class DesignDocService {

    @NonNull
    private ClassDefReader classDefReader;

    @NonNull
    private SequenceDiagramProcessor sequenceProcessor;

    @NonNull
    private ClassDiagramProcessor classProcessor;

    @NonNull
    private DiagramWriter<SequenceDiagram> sequenceWriter;

    @NonNull
    private DiagramWriter<ClassDiagram> classWriter;

    @NonNull
    private ClassDefRepository classDefRepository;

    @NonNull
    private InputSourceWatcher watcher;

    // key:classDef.sourceId, value:entrypoint
    private Map<String, Set<String>> entryPointMap = new HashMap<>();

    public void watchDir(Path srcDir, ClassDefChangeEventListener listener) {

        watcher.setContinue(true);
        try {
            Files.walk(srcDir).forEach(path -> watcher.watch(path.toFile().getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        watcher.start(inputSources -> {
            readSources(listener, inputSources);
        });
    }

    private void readSources(ClassDefChangeEventListener listener,
            Collection<String> inputSources) {

        classDefReader.init();

        Set<ClassDef> readDefs = inputSources.stream().map(Paths::get)
                .filter(path -> !Files.isDirectory(path)).filter(Files::isReadable)
                .map(classDefReader::readJava).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toSet());

        readDefs.forEach(classDefRepository::save);
        readDefs.forEach(clazz -> log.info("Read {}", clazz));

        Set<String> deletedIds = inputSources.stream().filter(s -> !Files.isDirectory(Paths.get(s)))
                .filter(sId -> !readDefs.stream()
                        .anyMatch(clazz -> StringUtils.equals(sId, clazz.getSourceId())))
                .collect(Collectors.toSet());
        deletedIds.forEach(clazz -> log.info("Remove {}", clazz));
        deletedIds.forEach(classDefRepository::remove);

        classDefRepository.solveReferences();

        Stream<String> entryPoints = readDefs.stream().map(ClassDef::getSourceId)
                .map(entryPointMap::get).filter(Objects::nonNull).flatMap(Set::stream).distinct();

        entryPoints.forEach(listener::onChange);
    }

    public List<String> getAllIds() {
        return classDefRepository.getEntryPoints();
    }

    public DesignDoc get(String designDocId) {

        MethodDef entryPoint = classDefRepository.findMethodByQualifiedSignature(designDocId);
        log.info("Build diagram for {}", entryPoint);

        LifeLineDef lifeLine = sequenceProcessor.process(entryPoint.getClassDef(), entryPoint);
        SequenceDiagram sequenceModel = SequenceDiagram.builder().entryLifeLine(lifeLine).build();
        ClassDiagram classModel = classProcessor.process(lifeLine);

        Stream<String> allSourceIds = Stream.of(sequenceModel, classModel)
                .map(DiagramModel::getAllSourceIds).flatMap(Set::stream).distinct();

        allSourceIds.forEach(sourceId -> {
            Set<String> entryPoints = entryPointMap.computeIfAbsent(sourceId,
                    key -> new HashSet<>());
            entryPoints.add(entryPoint.getQualifiedSignature());
        });

        Diagram sequenceDiagram = sequenceWriter.write(sequenceModel);
        Diagram classDiagram = classWriter.write(classModel);

        DesignDoc doc = new DesignDoc();
        doc.setId(designDocId);
        doc.setPkg(entryPoint.getClassDef().getPkg());
        doc.setClassName(entryPoint.getClassDef().getName());
        doc.add(sequenceDiagram);
        doc.add(classDiagram);

        return doc;
    }

    public List<DesignDoc> getAll() {
        List<DesignDoc> designDocs = getAllIds().stream().map(this::get)
                .collect(Collectors.toList());

        return designDocs;
    }

}
