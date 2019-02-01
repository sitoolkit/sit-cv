package io.sitoolkit.cv.core.app.config;

import java.nio.file.Path;

import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.ImplementCollector;
import io.sitoolkit.cv.core.domain.classdef.ImplementDetector;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.crud.CrudFinder;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.jsqlparser.CrudFinderJsqlparserImpl;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
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
import io.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServiceFactory {

    @Getter
    private ReportService reportService;

    @Getter
    private FunctionModelService functionModelService;

    @Getter
    private CrudService crudService;

    @Getter
    private ProjectManager projectManager;

    private ServiceFactory() {
    }

    public static ServiceFactory create(Path projectDir) {
        return new ServiceFactory().createServices(projectDir);
    }

    public static ServiceFactory createAndInitialize(Path projectDir) {
        return new ServiceFactory().createServices(projectDir).initialize();
    }

    public ServiceFactory initialize() {
        try {
            functionModelService.analyze();
        } catch (Exception e) {
            log.error("Exception initializing Code Visualizer", e);
        }
        return this;
    }

    protected ServiceFactory createServices(Path projectDir) {
        SitCvConfigReader configReader = new SitCvConfigReader();
        SitCvConfig config = configReader.read(projectDir);

        projectManager = new ProjectManager();
        projectManager.load(projectDir);

        ImplementDetector implementDetector = new ImplementDetector();
        ImplementCollector implementCollector = new ImplementCollector(implementDetector);

        functionModelService = createFunctionModelService(config, configReader, projectManager,
                implementDetector);

        reportService = createReportService(functionModelService, projectManager);

        crudService = createCrudService(functionModelService, projectManager, implementCollector);

        return this;
    }

    protected FunctionModelService createFunctionModelService(SitCvConfig config,
            SitCvConfigReader configReader, ProjectManager projectManager,
            ImplementDetector implementDetector) {
        ClassDefRepository classDefRepository = new ClassDefRepositoryMemImpl(config);
        ClassDefReader classDefReader = new ClassDefReaderJavaParserImpl(classDefRepository,
                projectManager, config);
        SequenceDiagramProcessor sequenceProcessor = new SequenceDiagramProcessor(config,
                implementDetector);
        ClassDiagramProcessor classProcessor = new ClassDiagramProcessor();
        GraphvizManager.initialize();
        PlantUmlWriter plantumlWriter = new PlantUmlWriter();
        DiagramWriter<SequenceDiagram> sequenceWriter = new SequenceDiagramWriterPlantUmlImpl(
                plantumlWriter);
        DiagramWriter<ClassDiagram> classWriter = new ClassDiagramWriterPlantUmlImpl(
                plantumlWriter);
        InputSourceWatcher watcher = new FileInputSourceWatcher();

        return new FunctionModelService(classDefReader, sequenceProcessor, classProcessor,
                sequenceWriter, classWriter, classDefRepository, watcher, projectManager,
                configReader);

    }

    protected ReportService createReportService(FunctionModelService functionModelService,
            ProjectManager projectManager) {
        FunctionModelReportProcessor functionModelReportProcessor = new FunctionModelReportProcessor();
        ReportWriter reportWriter = new ReportWriter();

        return new ReportService(functionModelReportProcessor, reportWriter, functionModelService,
                projectManager);
    }

    protected CrudService createCrudService(FunctionModelService functionModelService,
            ProjectManager projectManager, ImplementCollector implementCollector) {
        CrudFinder crudFinder = new CrudFinderJsqlparserImpl();
        CrudProcessor crudProcessor = new CrudProcessor(crudFinder, implementCollector);

        return new CrudService(functionModelService, crudProcessor, projectManager);
    }
}
