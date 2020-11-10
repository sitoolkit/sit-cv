package io.sitoolkit.cv.core.app.functionmodel;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.functionmodel.Diagram;
import io.sitoolkit.cv.core.domain.functionmodel.FunctionModel;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramModel;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import java.nio.file.Files;
import java.nio.file.Path;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;

@Slf4j
@RequiredArgsConstructor
public class FunctionModelService {

  @NonNull private ClassDefReader classDefReader;

  @NonNull private SequenceDiagramProcessor sequenceProcessor;

  @NonNull private ClassDiagramProcessor classProcessor;

  @NonNull private DiagramWriter<SequenceDiagram> sequenceWriter;

  @NonNull private DiagramWriter<ClassDiagram> classWriter;

  @NonNull private ClassDefRepository classDefRepository;

  @NonNull private ProjectManager projectManager;

  /** key:classDef.sourceId, value:entrypoint */
  private Map<String, Set<String>> entryPointMap = new HashMap<>();

  public void analyze() {
    StopWatch stopWatch = StopWatch.createStarted();
    projectManager.getCurrentProject().executeAllPreProcess();
    classDefReader.init().readDir();
    log.info("Analysis finished in {}", stopWatch);
  }

  public AnalysisResult analyze(Set<Path> srcFiles) {

    AnalysisResult result = new AnalysisResult();

    Set<String> entryPoitsBefore = new HashSet<>(classDefRepository.getEntryPoints());

    result.setEffectedSourceIds(readSources(srcFiles));

    Set<String> entryPoitsAfter = new HashSet<>(classDefRepository.getEntryPoints());

    result.setEntryPointModified(!entryPoitsBefore.equals(entryPoitsAfter));

    return result;
  }

  public synchronized Set<String> getEntryPoints() {
    return entryPointMap.values().stream()
        .filter(Objects::nonNull)
        .flatMap(Set::stream)
        .distinct()
        .collect(Collectors.toSet());
  }

  /**
   * @param sourcePaths file paths of source code to read.
   * @return stream of functionIds which are effected by input source.
   */
  private Stream<String> readSources(Collection<Path> sourcePaths) {

    Project currentProject = projectManager.getCurrentProject();
    currentProject.executeAllPreProcess();
    classDefReader.init();

    Set<ClassDef> readDefs =
        sourcePaths.stream()
            .filter(path -> !Files.isDirectory(path))
            .filter(Files::isReadable)
            .map(currentProject::findParseTarget)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(classDefReader::readJava)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());

    readDefs.forEach(classDefRepository::save);
    readDefs.forEach(clazz -> log.debug("Read {}", clazz));

    Set<String> deletedIds =
        sourcePaths.stream()
            .filter(s -> !Files.isDirectory(s))
            .map(Path::toString)
            .filter(
                sId ->
                    !readDefs.stream()
                        .anyMatch(clazz -> StringUtils.equals(sId, clazz.getSourceId())))
            .collect(Collectors.toSet());
    deletedIds.forEach(clazz -> log.debug("Remove {}", clazz));
    deletedIds.forEach(classDefRepository::remove);

    classDefRepository.solveReferences();

    return readDefs.stream()
        .map(ClassDef::getSourceId)
        .map(entryPointMap::get)
        .filter(Objects::nonNull)
        .flatMap(Set::stream)
        .distinct();
  }

  public List<String> getAllIds() {
    return classDefRepository.getEntryPoints();
  }

  public FunctionModel get(String functionId) {

    log.info("Build diagram for {}", functionId);
    MethodDef entryPoint = classDefRepository.findMethodByQualifiedSignature(functionId);

    LifeLineDef lifeLine = sequenceProcessor.process(entryPoint.getClassDef(), entryPoint);
    SequenceDiagram sequenceModel = SequenceDiagram.builder().entryLifeLine(lifeLine).build();
    ClassDiagram classModel = classProcessor.process(lifeLine);

    Stream<String> allSourceIds =
        Stream.of(sequenceModel, classModel)
            .map(DiagramModel::getAllSourceIds)
            .flatMap(Set::stream)
            .distinct();

    allSourceIds.forEach(
        sourceId -> {
          Set<String> entryPoints = entryPointMap.computeIfAbsent(sourceId, key -> new HashSet<>());
          entryPoints.add(entryPoint.getQualifiedSignature());
        });

    Diagram sequenceDiagram = sequenceWriter.write(sequenceModel);
    Diagram classDiagram = classWriter.write(classModel);

    FunctionModel model = new FunctionModel();
    model.setId(functionId);
    model.setPkg(entryPoint.getClassDef().getPkg());
    model.setClassName(entryPoint.getClassDef().getName());
    model.add(sequenceDiagram);
    model.add(classDiagram);

    return model;
  }

  public List<FunctionModel> getAll() {
    List<FunctionModel> functionModels =
        getAllIds().stream()
            .map(
                (functionId) -> {
                  try {
                    return get(functionId);
                  } catch (Exception e) {
                    log.warn("Exception when create diagram: functionId '{}'", functionId, e);
                    return null;
                  }
                })
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    return functionModels;
  }

  public List<ClassDef> getAllEntryPointClasses() {
    return classDefRepository.getAllEntryPointClasses();
  }
}
