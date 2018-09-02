package org.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.FileOutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.sitoolkit.cv.core.domain.classdef.ClassDef;
import org.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import org.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import org.sitoolkit.cv.core.domain.classdef.FieldDef;
import org.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
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

        // ClassDef classDef = repository
        // .findClassByQualifiedName("org.sitoolkit.cv.core.domain.classdef.ClassDef");
        ClassDef classDef = repository.findClassByQualifiedName("sample.ClassA");

        String uml = "@startuml\n" + write(classDef) + "\n@enduml";
        System.out.println(uml);

        SourceStringReader ssReader = new SourceStringReader(uml);

        FileOutputStream fos = new FileOutputStream("hoge.png");
        ssReader.outputImage(fos, new FileFormatOption(FileFormat.PNG));
    }

    String write(ClassDef clazz) {

        StringBuilder fieldPart = new StringBuilder();
        StringBuilder relationPart = new StringBuilder();

        clazz.getFields().stream().forEach(field -> {

            if (field.getTypeRef() != null) {
                relationPart.append(clazz.getName());
                relationPart.append(" -- ");
                relationPart.append(field.getTypeRef().getName());

            } else {
                fieldPart.append(hoge(clazz, field));
                fieldPart.append("\n");
            }

        });

        return relationPart + "\n\n" + fieldPart;
    }

    String hoge(ClassDef clazz, FieldDef field) {
        StringBuilder fieldPart = new StringBuilder();

        fieldPart.append(clazz.getName());
        fieldPart.append(" : ");
        fieldPart.append(field.getType());

        if (!field.getTypeParams().isEmpty()) {
            fieldPart.append("<");
            fieldPart.append(field.getTypeParams().stream().collect(Collectors.joining(",")));
            fieldPart.append(">");
        }

        fieldPart.append(" ");
        fieldPart.append(field.getName());

        return fieldPart.toString();
    }

    class ClassDiagramInfo {
        List<String> relations;
        List<List<String>> fields;
    }
}
