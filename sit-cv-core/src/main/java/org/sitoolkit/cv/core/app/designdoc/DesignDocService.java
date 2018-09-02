package org.sitoolkit.cv.core.app.designdoc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import org.sitoolkit.cv.core.domain.classdef.MethodDef;
import org.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import org.sitoolkit.cv.core.domain.designdoc.Diagram;
import org.sitoolkit.cv.core.domain.uml.DiagramWriter;
import org.sitoolkit.cv.core.domain.uml.LifeLineDef;
import org.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import org.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import org.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DesignDocService {

    @Resource
    ClassDefReader classDefReader;

    @Resource
    SequenceDiagramProcessor processor;

    @Resource
    DiagramWriter sequenceWriter;

    @Resource
    ClassDefRepository classDefRepository;

    @Resource
    InputSourceWatcher watcher;

    public void loadDir(Path srcDir) {

        classDefReader.readDir(srcDir);

    }

    public void watchDir(Path srcDir, ClassDefChangeEventListener listener) {
        watcher.setContinue(true);
        try {
            Files.walk(srcDir).forEach(path -> watcher.watch(path.toFile().getAbsolutePath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        watcher.start(inputSources -> {
            classDefReader.init(srcDir);

            inputSources.stream().forEach(inputSource -> {
                classDefReader.readJava(Paths.get(inputSource)).ifPresent(clazz -> {
                    classDefRepository.save(clazz);
                    classDefRepository.solveMethodCalls();
                    log.info("Read {}", clazz);

                    Set<String> entryPoints = entryPointMap.get(clazz.getSourceId());
                    if (entryPoints != null) {
                        entryPoints.stream().forEach(listener::onChange);
                    }
                });
            });
        });
    }

    public Set<String> getAllIds() {
        return classDefRepository.getEntryPoints();
    }

    // key:classDef.sourceId, value:entrypoint
    Map<String, Set<String>> entryPointMap = new HashMap<>();

    public DesignDoc get(String designDocId) {

        MethodDef entryPoint = classDefRepository.findMethodByQualifiedSignature(designDocId);
        log.info("Build diagram for {}", entryPoint);
        LifeLineDef lifeLine = processor.process(entryPoint.getClassDef(), entryPoint);

        lifeLine.getAllSourceIds().stream().forEach(sourceId -> {
            Set<String> entryPoints = entryPointMap.computeIfAbsent(sourceId,
                    key -> new HashSet<>());
            entryPoints.add(entryPoint.getQualifiedSignature());
        });

        Diagram diagram = sequenceWriter
                .write(SequenceDiagram.builder().entryLifeLine(lifeLine).build());

        DesignDoc doc = new DesignDoc();
        doc.add(diagram);

        return doc;
    }

}
