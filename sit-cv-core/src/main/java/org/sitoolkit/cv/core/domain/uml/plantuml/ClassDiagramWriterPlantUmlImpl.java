package org.sitoolkit.cv.core.domain.uml.plantuml;

import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.sitoolkit.cv.core.domain.classdef.ClassDef;
import org.sitoolkit.cv.core.domain.classdef.FieldDef;
import org.sitoolkit.cv.core.domain.classdef.MethodDef;
import org.sitoolkit.cv.core.domain.classdef.RelationDef;
import org.sitoolkit.cv.core.domain.designdoc.Diagram;
import org.sitoolkit.cv.core.domain.uml.ClassDiagram;
import org.sitoolkit.cv.core.domain.uml.DiagramWriter;
import org.sitoolkit.cv.core.domain.uml.RelationType;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.plantuml.StringUtils;

@Slf4j
public class ClassDiagramWriterPlantUmlImpl implements DiagramWriter<ClassDiagram>{

    private String class2str(ClassDef clazz) {

        String fieldsStr = clazz.getFields().stream().map(this::field2str).collect(Collectors.joining("\n"));
        String methodsStr = clazz.getMethods().stream()
                .sorted(Comparator.comparing(MethodDef::isPublic).reversed())
                .map(this::method2str)
                .collect(Collectors.joining("\n"));

        String classStr = String.format("class %s {\n"
                + "%s\n"
                + "%s\n"
                + "}\n"
                , clazz.getName(), fieldsStr, methodsStr);

        return classStr;
    }

    private String method2str(MethodDef method) {
        return String.format("%s%s()",
                method.isPublic() ? "+" : "", // TODO public以外のアクセス制御子
                method.getName());
    }

    private String field2str(FieldDef field) {
        return String.format("%s%s : %s",
                "", // TODO アクセス制御子
                field.getName(),
                getTypeStr(field));
    }

    private String rel2str(RelationDef rel) {
        return String.format("%s %s %s %s %s : %s",
                rel.getSelf().getName(),
                rel.getSelfCardinality(),
                relType2str(rel.getType()),
                rel.getOtherCardinality(),
                rel.getOther().getName(),
                StringUtils.isEmpty(rel.getDescription()) ? "" : (rel.getDescription() + " >"));
    }

    private String relType2str(RelationType relType) {
        switch (relType) {
        case DEPENDENCY:
            return "."; // 横方向の破線

        default: //TODO 他の関係
            return "--";
        }
    }

    private String getTypeStr(FieldDef field) {
        if (field.getTypeParams().isEmpty()) {
            return field.getType();
        } else {
            return field.getType() +
                    field.getTypeParams().stream().collect(Collectors.joining(",", "<", ">"));
        }
    }

    public String serialize(ClassDiagram classDiagram) {
        String umlString= Stream.of(
                Stream.of("@startuml"),
                classDiagram.getClasses().stream().map(this::class2str),
                classDiagram.getRelations().stream().map(this::rel2str),
                Stream.of("@enduml"))
                .flatMap(Function.identity())
                .collect(Collectors.joining(System.lineSeparator()));

        log.debug("serializedDiagram -> {}", umlString);

        return umlString;
    }

    @Override
    public Diagram write(ClassDiagram diagram) {
        return PlantUmlUtil.createDiagram(diagram, this::serialize);
    }

}
