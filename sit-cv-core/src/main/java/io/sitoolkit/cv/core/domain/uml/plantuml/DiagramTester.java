package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;

import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.infra.config.Config;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class DiagramTester {

    public static void main(String[] args) throws Exception {
        new DiagramTester().execute();
    }

    void execute() throws Exception {
        ClassDefRepository repository = new ClassDefRepositoryMemImpl();
        Config config = new Config();
        ClassDefReader reader = new ClassDefReaderJavaParserImpl(repository, config);
        ClassDefRepositoryParam param = new ClassDefRepositoryParam();
        // param.getBinDirs().add(Paths.get("target/classes"));
        // param.setJarList(Paths.get("jar-list.txt"));
        param.getSrcDirs().add(Paths.get("../sample/src/test/java"));
        reader.init(param);
        // reader.readDir(Paths.get("src/main/java"));
        reader.readDir(Paths.get("../sample/src/test/java"));

        MethodDef entryPoint = repository
                .findMethodByQualifiedSignature("sample.SequenceClass1.entryPoint()");
        outputImage(getSequenceDiagramUML(entryPoint), "sequence.png");
        outputImage(getClassDiagramUML(entryPoint), "class.png");

    }

    String getSequenceDiagramUML(MethodDef entryPoint) {
        SequenceDiagramWriterPlantUmlImpl writer = new SequenceDiagramWriterPlantUmlImpl(
                new PlantUmlWriter());
        SequenceDiagramProcessor processor = new SequenceDiagramProcessor();
        LifeLineDef entryLifeLine = processor.process(entryPoint.getClassDef(), entryPoint);
        SequenceDiagram sd = SequenceDiagram.builder().entryLifeLine(entryLifeLine).build();
        return writer.serialize(sd);
    }

    String getClassDiagramUML(MethodDef entryPoint) {
        ClassDiagramWriterPlantUmlImpl writer = new ClassDiagramWriterPlantUmlImpl(
                new PlantUmlWriter());
        ClassDiagramProcessor processor = new ClassDiagramProcessor();
        return writer.serialize(processor.process(entryPoint));
    }

    void outputImage(String uml, String fileName) throws IOException {
        SourceStringReader ssReader = new SourceStringReader(uml);
        FileOutputStream fos = new FileOutputStream(fileName);
        ssReader.outputImage(fos, new FileFormatOption(FileFormat.PNG));
    }
}
