package io.sitoolkit.cv.core.app.designdoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilterConditionReader;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesignDocService {

    @Resource
    ClassDefReader classDefReader;

    @Resource
    SequenceDiagramProcessor sequenceProcessor;

    @Resource
    ClassDiagramProcessor classProcessor;

    @Resource
    DiagramWriter<SequenceDiagram> sequenceWriter;

    @Resource
    DiagramWriter<ClassDiagram> classWriter;

    @Resource
    ClassDefFilter classFilter;

    @Resource
    ClassDefRepository classDefRepository;

    @Resource
    InputSourceWatcher watcher;

    public void loadDir(Path projDir, Path srcDir) {

        classDefReader.readDir(srcDir);
        ClassDefFilterConditionReader.read(projDir).ifPresent(classFilter::setCondition);

    }

    public void watchDir(Path srcDir, ClassDefChangeEventListener listener) {

        watcher.setContinue(true);
        try {
            Files.walk(srcDir).forEach(path -> watcher.watch(path.toFile().getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        watcher.start(inputSources -> {
            readSources(srcDir, listener, inputSources);
        });
    }


    private void readSources(Path srcDir, ClassDefChangeEventListener listener, Collection<String> inputSources) {

        classDefReader.init(srcDir);

        Set<ClassDef> readDefs = inputSources.stream()
                .map(Paths::get)
                .map(classDefReader::readJava)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());

        readDefs.forEach(classDefRepository::save);
        readDefs.forEach(clazz -> log.info("Read {}", clazz));
        classDefRepository.solveReferences();

        Stream<String> entryPoints = readDefs.stream()
                .map(ClassDef::getSourceId)
                .map(entryPointMap::get)
                .filter(Objects::nonNull)
                .flatMap(Set::stream)
                .distinct();

        entryPoints.forEach(listener::onChange);
    }

    public Set<String> getAllIds() {
        return classDefRepository.getEntryPoints();
    }

    // key:classDef.sourceId, value:entrypoint
    Map<String, Set<String>> entryPointMap = new HashMap<>();

    public DesignDoc get(String designDocId) {

        MethodDef entryPoint = classDefRepository.findMethodByQualifiedSignature(designDocId);
        log.info("Build diagram for {}", entryPoint);
        LifeLineDef lifeLine = sequenceProcessor.process(entryPoint.getClassDef(), entryPoint);

        lifeLine.getAllSourceIds().stream().forEach(sourceId -> {
            Set<String> entryPoints = entryPointMap.computeIfAbsent(sourceId,
                    key -> new HashSet<>());
            entryPoints.add(entryPoint.getQualifiedSignature());
        });

        Diagram sequenceDiagram = sequenceWriter
                .write(SequenceDiagram.builder().entryLifeLine(lifeLine).build());

        Diagram classDiagram = classWriter.write(classProcessor.process(entryPoint));

        DesignDoc doc = new DesignDoc();
        doc.add(sequenceDiagram);
        doc.add(classDiagram);

        return doc;
    }

}
