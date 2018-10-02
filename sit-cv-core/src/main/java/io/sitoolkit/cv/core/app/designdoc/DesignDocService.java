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
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;

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

    Map<Pair<Path, ClassDefChangeEventListener>, Timer> timerMap = new ConcurrentHashMap<>();

    Map<Pair<Path, ClassDefChangeEventListener>, Set<String>> waitingMap = new ConcurrentHashMap<>();

    final long RELOAD_WAIT_TIME_MILLIS = 300;

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

        Pair<Path, ClassDefChangeEventListener> key = Pair.of(srcDir, listener);

        watcher.start(inputSources -> {
            timerMap.computeIfPresent(key, (k, t) -> {
                t.cancel();
                log.debug("waiting timer task cancelled");
                return null;
            });
            Timer timer = new Timer();
            timer.schedule(createTimerTask(key), RELOAD_WAIT_TIME_MILLIS);
            log.debug("new timer task created");
            timerMap.put(key, timer);

            waitingMap.putIfAbsent(key, new HashSet<>());
            Set<String> waitingSources = waitingMap.get(key);
            synchronized (waitingSources) {
                waitingSources.addAll(inputSources);
                log.debug("added inputSources: {} , {}", waitingSources, key);
            }
        });
    }

    private TimerTask createTimerTask(Pair<Path, ClassDefChangeEventListener> key) {

        return new TimerTask() {
            @Override
            public void run() {
                Set<String> inputSources = new HashSet<>();
                Set<String> waitingSources = waitingMap.get(key);
                synchronized (waitingSources) {
                    inputSources.addAll(waitingSources);
                    waitingSources.clear();
                }
                readSources(key.getLeft(), key.getRight(), inputSources);
            }
        };
    }

    private void readSources(Path srcDir, ClassDefChangeEventListener listener, Collection<String> inputSources) {

        log.debug("Read Sources start : sources = {}, dir = {}, listener = {}", inputSources, srcDir, listener);
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
