package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.classdef.FieldDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.RelationDef;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.RelationType;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class ClassDiagramWriterPlantUmlImpl implements DiagramWriter<ClassDiagram> {

    @NonNull
    PlantUmlWriter plantumlWriter;

    IdentiferFormatter idFormatter = new IdentiferFormatter();

    private String class2str(ClassDef clazz) {

        String fieldsStr = clazz.getFields().stream().map(this::field2str)
                .collect(Collectors.joining("\n"));
        String methodsStr = clazz.getMethods().stream()
                .sorted(Comparator.comparing(MethodDef::isPublic).reversed()
                        .thenComparing(Comparator.comparing(MethodDef::getName)))
                .map(this::method2str).collect(Collectors.joining("\n"));

        String classStr = String.format("class %s {\n" + "%s\n" + "%s\n" + "}\n", clazz.getName(),
                fieldsStr, methodsStr);

        return classStr;
    }

    private String method2str(MethodDef method) {
        return String.format("%s%s : %s", method.isPublic() ? "+" : "", // TODO
                                                                        // public以外のアクセス制御子
                idFormatter.format(method.getSignature()),
                idFormatter.format(method.getReturnType().toString()));
    }

    private String field2str(FieldDef field) {
        return String.format("%s%s : %s", "", // TODO アクセス制御子
                field.getName(), idFormatter.format(field.getType().toString()));
    }

    private String rel2str(RelationDef rel) {
        return String
                .format("%s %s %s %s %s %s", rel.getSelf().getName(),
                        StringUtils.isEmpty(rel.getSelfCardinality()) ? ""
                                : ("\"" + rel.getSelfCardinality() + "\""),
                        relType2str(rel.getType()),
                        StringUtils.isEmpty(rel.getOtherCardinality()) ? ""
                                : ("\"" + rel.getOtherCardinality() + "\""),
                        rel.getOther().getName(), StringUtils.isEmpty(rel.getDescription()) ? ""
                                : ": " + (rel.getDescription() + " >"));
    }

    private String relType2str(RelationType relType) {
        switch (relType) {
            case DEPENDENCY:
                return "."; // horizontal dotted line

            case OWNERSHIP:
                return "-->"; // vertical arrow

            default: // TODO other relation
                return "--";
        }
    }

    public String serialize(ClassDiagram classDiagram) {
        String umlString = Stream
                .of(Stream.of("@startuml"), classDiagram.getClasses().stream().map(this::class2str),
                        classDiagram.getRelations().stream().map(this::rel2str),
                        Stream.of("@enduml"))
                .flatMap(Function.identity()).collect(Collectors.joining(System.lineSeparator()));

        log.debug("serializedDiagram -> {}", umlString);

        return umlString;
    }

    @Override
    public Diagram write(ClassDiagram diagram) {
        return plantumlWriter.createDiagram(diagram, this::serialize);
    }
}
