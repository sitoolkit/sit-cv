package org.sitoolkit.design;

import org.sitoolkit.cv.core.app.designdoc.DesignDocService;
import org.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import org.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import org.sitoolkit.cv.core.domain.uml.ClassDiagram;
import org.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import org.sitoolkit.cv.core.domain.uml.DiagramWriter;
import org.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import org.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import org.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import org.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl;
import org.sitoolkit.cv.core.infra.config.Config;
import org.sitoolkit.cv.core.infra.watcher.FileInputSourceWatcher;
import org.sitoolkit.cv.core.infra.watcher.InputSourceWatcher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
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
    public Config config() {
        return new Config();
    }
}
