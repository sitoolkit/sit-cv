package org.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.sitoolkit.cv.core.domain.designdoc.Diagram;
import org.sitoolkit.cv.core.domain.uml.LifeLineDef;
import org.sitoolkit.cv.core.domain.uml.MessageDef;
import org.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import org.sitoolkit.cv.core.domain.uml.DiagramWriter;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class SequenceDiagramWriterPlantUmlImpl implements DiagramWriter {

    public List<String> write(List<SequenceDiagram> diagrams) {
        List<String> lines = new ArrayList<>();
        lines.add("@startuml");

        lines.addAll(diagrams.stream().map(diagram -> lifeline2str(diagram.getEntryLifeLine()))
                .flatMap(List::stream).collect(Collectors.toList()));

        lines.add("@enduml");

        return lines;
    }

    @Override
    public Diagram write(SequenceDiagram sequence) {
        Diagram diagram = new Diagram();
        diagram.setId(sequence.getId());
        diagram.setTags(sequence.getAllTags());

        SourceStringReader reader = new SourceStringReader(serialize(sequence));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            reader.outputImage(baos, new FileFormatOption(FileFormat.PNG));
            diagram.setData(baos.toByteArray());

            return diagram;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String serialize(SequenceDiagram diagram) {
        List<String> lines = new ArrayList<>();
        lines.add("@startuml");

        lines.addAll(lifeline2str(diagram.getEntryLifeLine()));

        lines.add("@enduml");

        return lines.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    private List<String> lifeline2str(LifeLineDef lifeLine) {
        return lifeLine.getMessages().stream().map(message -> message2str(lifeLine, message))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    private List<String> message2str(LifeLineDef lifeLine, MessageDef message) {
        LifeLineDef target = message.getTarget();
        List<String> list = lifeline2str(target);

        list.add(0, lifeLine.getObjectName() + " -> " + target.getObjectName() + " :"
                + message.getName());

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
