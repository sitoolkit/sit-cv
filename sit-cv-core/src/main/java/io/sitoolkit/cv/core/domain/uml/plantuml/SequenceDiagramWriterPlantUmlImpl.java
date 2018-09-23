package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.DiagramWriter;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.MessageDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramWriterPlantUmlImpl implements DiagramWriter<SequenceDiagram> {

    @Resource
    PlantUmlWriter plantumlWriter;

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

        lines.addAll(lifeline2str(diagram.getEntryLifeLine()));

        lines.add("@enduml");

        String umlString = lines.stream().collect(Collectors.joining(System.lineSeparator()));

        log.debug(umlString);

        return umlString;
    }

    private List<String> lifeline2str(LifeLineDef lifeLine) {
        return lifeLine.getMessages().stream().map(message -> message2str(lifeLine, message))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> message2str(LifeLineDef lifeLine, MessageDef message) {
        LifeLineDef target = message.getTarget();
        List<String> list = lifeline2str(target);

        list.add(0, lifeLine.getObjectName() + " -> " + target.getObjectName() + " :"
                + "[[#{" + target.getComment().replaceAll("\r\n", "&#13;&#10;") + "} " + message.getRequestName() + "]]");

        if (!StringUtils.equals(message.getResponseName(), "void")) {
            list.add(lifeLine.getObjectName() + " <-- " + target.getObjectName() + " :"
                    + message.getResponseName());
        }

        return list;
    }

    public void writeToFile(List<SequenceDiagram> diagrams, Path filePath) {
        try {
            Files.write(filePath, write(diagrams));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
