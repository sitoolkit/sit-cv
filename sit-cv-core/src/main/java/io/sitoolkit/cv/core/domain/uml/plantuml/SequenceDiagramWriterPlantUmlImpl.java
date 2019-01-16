package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import io.sitoolkit.cv.core.domain.function.Diagram;
import io.sitoolkit.cv.core.domain.uml.BranchSequenceElement;
import io.sitoolkit.cv.core.domain.uml.CatchSequenceGroup;
import io.sitoolkit.cv.core.domain.uml.ConditionalSequenceGroup;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.FinallySequenceGroup;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.LoopSequenceGroup;
import io.sitoolkit.cv.core.domain.uml.MessageDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceElement;
import io.sitoolkit.cv.core.domain.uml.SequenceElementWriter;
import io.sitoolkit.cv.core.domain.uml.TrySequenceGroup;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class SequenceDiagramWriterPlantUmlImpl
        implements DiagramWriter<SequenceDiagram>, SequenceElementWriter {
    private final String PARAM_INDENT = "  ";

    @NonNull
    PlantUmlWriter plantumlWriter;

    IdentiferFormatter idFormatter = new IdentiferFormatter();

    public List<String> write(List<SequenceDiagram> diagrams) {
        List<String> lines = new ArrayList<>();
        lines.add("@startuml");

        lines.addAll(diagrams.stream().map(diagram -> lifeline2str(diagram.getEntryLifeLine()))
                .flatMap(List::stream).collect(Collectors.toList()));

        lines.add("@enduml");

        return lines;
    }

    @Override
    public Diagram write(SequenceDiagram diagram) {
        return plantumlWriter.createDiagram(diagram, this::serialize);
    }

    public String serialize(SequenceDiagram diagram) {
        List<String> lines = new ArrayList<>();
        lines.add("@startuml");

        lines.addAll(message2str("[", diagram.getEntryLifeLine().getEntryMessage()));

        lines.add("@enduml");

        String umlString = lines.stream().collect(Collectors.joining(System.lineSeparator()));

        log.info("Serialized Diagram :\n{}", umlString);

        return umlString;
    }

    protected List<String> lifeline2str(LifeLineDef lifeLine) {
        List<String> lifeLineStrings = lifeLine.getElements().stream()
                .map(element -> element.write(lifeLine, this)).flatMap(List::stream)
                .collect(Collectors.toList());
        lifeLineStrings.add(0, "activate " + lifeLine.getObjectName());
        lifeLineStrings.add("deactivate " + lifeLine.getObjectName());
        return lifeLineStrings;
    }

    protected List<String> message2str(String sourceName, MessageDef message) {
        LifeLineDef target = message.getTarget();
        List<String> list = lifeline2str(target);

        list.add(0,
                sourceName + "-> " + target.getObjectName() + " :" + "[[#{"
                        + message.getRequestQualifiedSignature() + "} "
                        + idFormatter.format(buildRequestName(message)) + "]]");

        String responseName = type2Str(message.getResponseType());
        if (!StringUtils.equals(responseName, "void")) {
            list.add(list.size() - 1, sourceName + "<-- " + target.getObjectName() + " :"
                    + idFormatter.format(responseName));
        }

        return list;
    }

    protected String buildRequestName(MessageDef message) {
        String paramNames = "";
        if (message.getRequestParamTypes().size() > 0) {
            String separator = plantumlWriter.LINE_SEPARATOR + PARAM_INDENT;
            paramNames = separator + message.getRequestParamTypes().stream().map(this::type2Str)
                    .collect(Collectors.joining("," + separator));
        }
        return message.getRequestName() + "(" + paramNames + ")";
    }

    protected String type2Str(TypeDef type) {
        if (type.getVariable() == null) {
            return type.toString();
        } else {
            return type.getVariable() + ": " + type.toString();
        }
    }

    public void writeToFile(List<SequenceDiagram> diagrams, Path filePath) {
        try {
            Files.write(filePath, write(diagrams));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> elements2str(LifeLineDef lifeLine, List<? extends SequenceElement> elements) {
        return elements.stream().map(childElement -> childElement.write(lifeLine, this))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, LoopSequenceGroup group) {
        List<String> list = new ArrayList<>();

        list.add("loop " + escapeLineSeparator(group.getScope()));

        list.addAll(elements2str(lifeLine, group.getElements()));

        list.add("end");

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, ConditionalSequenceGroup group) {
        List<String> list = new ArrayList<>();

        String altType = group.isFirst() ? "alt" : "else";

        list.add(altType + " " + escapeLineSeparator(group.getCondition()));

        list.addAll(elements2str(lifeLine, group.getElements()));

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, BranchSequenceElement group) {
        List<String> list = new ArrayList<>();

        list.addAll(elements2str(lifeLine, group.getConditions()));

        list.add("end");

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, TrySequenceGroup group) {
        List<String> list = new ArrayList<>();

        list.add("group try");

        list.addAll(elements2str(lifeLine, group.getElements()));

        list.addAll(elements2str(lifeLine, group.getCatchGroups()));

        if (group.getFinallyGroup() != null) {
            list.addAll(group.getFinallyGroup().write(lifeLine, this));
        }

        list.add("end");

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, CatchSequenceGroup group) {
        List<String> list = new ArrayList<>();

        list.add("else catch " + escapeLineSeparator(group.getParameter()));

        list.addAll(elements2str(lifeLine, group.getElements()));

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, FinallySequenceGroup group) {
        List<String> list = new ArrayList<>();

        list.add("else finally");

        list.addAll(elements2str(lifeLine, group.getElements()));

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, MessageDef message) {
        return message2str(lifeLine.getObjectName(), message);
    }

    private String escapeLineSeparator(String str) {
        return str.replaceAll("(\r\n|\n)", plantumlWriter.ESCAPED_LINE_SEPARATOR);
    }
}
