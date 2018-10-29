package io.sitoolkit.cv.core.app.config;

import java.nio.file.Path;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.designdoc.DesignDocReportProcessor;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor2;
import io.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.domain.uml.plantuml.PlantUmlWriter;
import io.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl2;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import io.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import lombok.Getter;

public class ServiceFactory {

    @Getter
    private ReportService reportService;

    @Getter
    private DesignDocService designDocService;

    @Getter
    private ProjectManager projectManager;

    private ServiceFactory() {
    }

    public static ServiceFactory initialize(Path projectDir) {
        return new ServiceFactory().init(projectDir);
    }

    protected ServiceFactory init(Path projectDir) {
        SitCvConfig cvConfig = SitCvConfig.load(projectDir);

        projectManager = new ProjectManager();
        projectManager.load(projectDir);

        designDocService = buildDesignDocService(cvConfig, projectManager);
        reportService = buildReportService(designDocService, projectManager);

        return this;
    }

    protected DesignDocService buildDesignDocService(SitCvConfig config,
            ProjectManager projectManager) {
        ClassDefRepository classDefRepository = new ClassDefRepositoryMemImpl(config);
        ClassDefReader classDefReader = new ClassDefReaderJavaParserImpl(classDefRepository,
                projectManager, config).init().readDir();
        SequenceDiagramProcessor sequenceProcessor = new SequenceDiagramProcessor2(
                config.getSequenceDiagramFilter());
        ClassDiagramProcessor classProcessor = new ClassDiagramProcessor();
        GraphvizManager.initialize();
        PlantUmlWriter plantumlWriter = new PlantUmlWriter();
        DiagramWriter<SequenceDiagram> sequenceWriter = new SequenceDiagramWriterPlantUmlImpl2(
                plantumlWriter);
        DiagramWriter<ClassDiagram> classWriter = new ClassDiagramWriterPlantUmlImpl(
                plantumlWriter);
        InputSourceWatcher watcher = new FileInputSourceWatcher();

        return new DesignDocService(classDefReader, sequenceProcessor, classProcessor,
                sequenceWriter, classWriter, classDefRepository, watcher);

    }

    protected ReportService buildReportService(DesignDocService designDocService,
            ProjectManager projectManager) {
        DesignDocReportProcessor designDocReportProcessor = new DesignDocReportProcessor();
        ReportWriter reportWriter = new ReportWriter();
        return new ReportService(designDocReportProcessor, reportWriter, designDocService,
                projectManager);
    }
}
