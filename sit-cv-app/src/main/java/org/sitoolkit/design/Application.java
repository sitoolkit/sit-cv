package org.sitoolkit.design;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.domain.uml.plantuml.PlantUmlWriter;
import io.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.infra.config.Config;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import io.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import io.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationConfig applicationConfig() {
        return new ApplicationConfig();
    }

    @Bean
    public SequenceDiagramProcessor sequenceDiagramProcessor() {
        return new SequenceDiagramProcessor();
    }

    @Bean
    public ClassDiagramProcessor classDiagramProcessor() {
        return new ClassDiagramProcessor();
    }

    @Bean
    public DesignDocService designService() {
        return new DesignDocService();
    }

    @Bean
    public DiagramWriter<SequenceDiagram> sequenceDiagramWriter() {
        return new SequenceDiagramWriterPlantUmlImpl();
    }

    @Bean
    public DiagramWriter<ClassDiagram> classDiagramWriter() {
        return new ClassDiagramWriterPlantUmlImpl();
    }

    @Bean
    public ClassDefRepository classDefRepository() {
        return new ClassDefRepositoryMemImpl();
    }

    @Bean
    public ClassDefReader classDefReader() {
        return new ClassDefReaderJavaParserImpl();
    }

    @Bean
    public InputSourceWatcher inputSourceWatcher() {
        return new FileInputSourceWatcher();
    }

    @Bean
    public PlantUmlWriter plantumlWriter() {
        return new PlantUmlWriter();
    }

    @Bean
    public GraphvizManager graphvizManager() {
        return new GraphvizManager();
    }

    @Bean
    public Config config() {
        return new Config();
    }
}
