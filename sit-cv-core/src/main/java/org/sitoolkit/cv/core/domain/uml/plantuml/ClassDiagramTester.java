package org.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.FileOutputStream;
import java.nio.file.Paths;

import org.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import org.sitoolkit.cv.core.domain.classdef.MethodDef;
import org.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import org.sitoolkit.cv.core.domain.uml.ClassDiagram;
import org.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import org.sitoolkit.cv.core.infra.config.Config;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class ClassDiagramTester {

    public static void main(String[] args) throws Exception {
        new ClassDiagramTester().execute();
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

        repository.solveClassRefs();


        ClassDiagramWriterPlantUmlImpl writer = new ClassDiagramWriterPlantUmlImpl();
        ClassDiagramProcessor processor = new ClassDiagramProcessor();

        // ClassDef classDef = repository
        // .findClassByQualifiedName("org.sitoolkit.cv.core.domain.classdef.ClassDef");
//        ClassDef classDef = repository.findClassByQualifiedName("sample.ClassA");
        MethodDef entryPoint = repository.findMethodByQualifiedSignature("sample.ClassA.publicMethod()");
        ClassDiagram cd = processor.process(entryPoint);

        String uml = writer.serialize(cd);

        SourceStringReader ssReader = new SourceStringReader(uml);

        FileOutputStream fos = new FileOutputStream("hoge.png");
        ssReader.outputImage(fos, new FileFormatOption(FileFormat.PNG));
    }

}
