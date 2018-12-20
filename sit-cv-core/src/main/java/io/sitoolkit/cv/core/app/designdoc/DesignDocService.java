package io.sitoolkit.cv.core.app.designdoc;

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
import org.apache.commons.lang3.time.StopWatch;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
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

    @NonNull
    private ProjectManager projectManager;

    /**
     * key:classDef.sourceId, value:entrypoint
     */
    private Map<String, Set<String>> entryPointMap = new HashMap<>();

    public void analyze() {
        StopWatch stopWatch = StopWatch.createStarted();
        projectManager.getCurrentProject().executeAllPreProcess();
        classDefReader.init().readDir();
        log.info("Analysis finished in {}", stopWatch);
    }

    public void watchDir(Path srcDir, DesignDocChangeEventListener listener) {

        watcher.setContinue(true);
        watcher.watch(srcDir.toString());
        watcher.start(inputSources -> {
            int entryPoitSizeBefore = classDefRepository.getEntryPoints().size();

            readSources(inputSources).forEach(listener::onDesignDocChange);

            if (classDefRepository.getEntryPoints().size() != entryPoitSizeBefore) {
                listener.onDesignDocListChange();
            }

        });
    }

    /**
     *
     * @param sourcePaths
     *            file paths of source code to read.
     * @return stream of designDocIds which are effected by input source.
     */
    private Stream<String> readSources(Collection<String> sourcePaths) {

        Project currentProject = projectManager.getCurrentProject();
        currentProject.executeAllPreProcess();
        classDefReader.init();

        Set<ClassDef> readDefs = sourcePaths.stream().map(Paths::get)
                .filter(path -> !Files.isDirectory(path)).filter(Files::isReadable)
                .map(currentProject::findParseTarget).filter(Optional::isPresent).map(Optional::get)
                .map(classDefReader::readJava).filter(Optional::isPresent).map(Optional::get)
                .collect(Collectors.toSet());

        readDefs.forEach(classDefRepository::save);
        readDefs.forEach(clazz -> log.debug("Read {}", clazz));

        Set<String> deletedIds = sourcePaths.stream().filter(s -> !Files.isDirectory(Paths.get(s)))
                .filter(sId -> !readDefs.stream()
                        .anyMatch(clazz -> StringUtils.equals(sId, clazz.getSourceId())))
                .collect(Collectors.toSet());
        deletedIds.forEach(clazz -> log.debug("Remove {}", clazz));
        deletedIds.forEach(classDefRepository::remove);

        classDefRepository.solveReferences();

        return readDefs.stream().map(ClassDef::getSourceId).map(entryPointMap::get)
                .filter(Objects::nonNull).flatMap(Set::stream).distinct();

    }

    public List<String> getAllIds() {
        return classDefRepository.getEntryPoints();
    }

    public DesignDoc get(String designDocId) {

        log.info("Build diagram for {}", designDocId);
        MethodDef entryPoint = classDefRepository.findMethodByQualifiedSignature(designDocId);

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
        List<DesignDoc> designDocs = getAllIds().stream().map((designDocId) -> {
            try {
                return get(designDocId);
            } catch (Exception e) {
                log.warn("Exception when create diagram: designDocId '{}'", designDocId, e);
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toList());

        return designDocs;
    }

}
