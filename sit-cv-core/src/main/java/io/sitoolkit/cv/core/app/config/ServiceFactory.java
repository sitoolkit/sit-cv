package io.sitoolkit.cv.core.app.config;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.crud.CrudFinder;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.jsqlparser.CrudFinderJsqlparserImpl;
import io.sitoolkit.cv.core.domain.designdoc.DesignDocMenuBuilder;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.domain.project.gradle.GradleProjectReader;
import io.sitoolkit.cv.core.domain.project.maven.MavenProjectReader;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.crud.CrudReportProcessor;
import io.sitoolkit.cv.core.domain.report.designdoc.DesignDocReportProcessor;
import io.sitoolkit.cv.core.domain.report.functionmodel.FunctionModelReportProcessor;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.domain.uml.plantuml.PlantUmlWriter;
import io.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceFactory {

  @Getter
  private FunctionModelService functionModelService;

  @Getter
  private DesignDocService designDocService;

  @Getter
  private CrudService crudService;

  @Getter
  private ReportService reportService;

  @Getter
  private ProjectManager projectManager;

  private ServiceFactory() {
  }

  public static ServiceFactory create(Path projectDir, boolean watch) {
    return new ServiceFactory().createServices(projectDir, watch);
  }

  public static ServiceFactory createAndInitialize(Path projectDir, boolean watch) {
    return new ServiceFactory().createServices(projectDir, watch).initialize();
  }

  public ServiceFactory initialize() {
    try {
      functionModelService.analyze();
    } catch (Exception e) {
      log.error("Exception initializing Code Visualizer", e);
    }
    return this;
  }

  protected ServiceFactory createServices(Path projectDir, boolean watch) {
    SitCvConfigReader configReader = new SitCvConfigReader();
    SitCvConfig config = configReader.read(projectDir, watch);

    projectManager = createProjectManager(config);
    projectManager.load(projectDir);

    functionModelService = createFunctionModelService(config, projectManager);

    designDocService = createDesignDocService(functionModelService);

    crudService = createCrudService(functionModelService, projectManager);

    reportService = createReportService(functionModelService, designDocService, crudService,
        projectManager);

    return this;
  }

  protected ProjectManager createProjectManager(SitCvConfig config) {
    SqlLogProcessor sqlLogProcessor = new SqlLogProcessor();
    List<ProjectReader> readers = Arrays.asList(new MavenProjectReader(sqlLogProcessor),
        new GradleProjectReader(sqlLogProcessor));

    return new ProjectManager(readers, config);
  }

  protected FunctionModelService createFunctionModelService(SitCvConfig config,
      ProjectManager projectManager) {
    ClassDefRepository classDefRepository = new ClassDefRepositoryMemImpl(config);
    ClassDefReader classDefReader = new ClassDefReaderJavaParserImpl(classDefRepository,
        projectManager, config);
    SequenceDiagramProcessor sequenceProcessor = new SequenceDiagramProcessor(config);
    ClassDiagramProcessor classProcessor = new ClassDiagramProcessor();
    GraphvizManager.initialize();
    PlantUmlWriter plantumlWriter = new PlantUmlWriter();
    DiagramWriter<SequenceDiagram> sequenceWriter = new SequenceDiagramWriterPlantUmlImpl(
        plantumlWriter);
    DiagramWriter<ClassDiagram> classWriter = new ClassDiagramWriterPlantUmlImpl(plantumlWriter);

    return new FunctionModelService(classDefReader, sequenceProcessor, classProcessor,
        sequenceWriter, classWriter, classDefRepository, projectManager);

  }

  protected DesignDocService createDesignDocService(FunctionModelService functionModelService) {
    DesignDocMenuBuilder menuBuilder = new DesignDocMenuBuilder();
    return new DesignDocService(functionModelService, menuBuilder);
  }

  protected CrudService createCrudService(FunctionModelService functionModelService,
      ProjectManager projectManager) {
    CrudFinder crudFinder = new CrudFinderJsqlparserImpl();
    CrudProcessor crudProcessor = new CrudProcessor(crudFinder);

    return new CrudService(functionModelService, crudProcessor, projectManager);
  }

  protected ReportService createReportService(FunctionModelService functionModelService,
      DesignDocService designDocService, CrudService crudService, ProjectManager projectManager) {
    FunctionModelReportProcessor functionModelReportProcessor = new FunctionModelReportProcessor();
    DesignDocReportProcessor designDocReportProcessor = new DesignDocReportProcessor();
    CrudReportProcessor crudReportProcessor = new CrudReportProcessor();
    ReportWriter reportWriter = new ReportWriter();

    return new ReportService(functionModelReportProcessor, designDocReportProcessor,
        crudReportProcessor, reportWriter, functionModelService, designDocService, crudService,
        projectManager);
  }

}
