package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.MessageDef;
import io.sitoolkit.cv.core.domain.uml.SequenceElementWriter;
import io.sitoolkit.cv.core.domain.uml.SequenceGroup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SequenceDiagramWriterPlantUmlImpl2 extends SequenceDiagramWriterPlantUmlImpl
        implements SequenceElementWriter {

    @NonNull
    PlantUmlWriter plantumlWriter;

    IdentiferFormatter idFormatter = new IdentiferFormatter();

    public SequenceDiagramWriterPlantUmlImpl2(PlantUmlWriter plantumlWriter) {
        super(plantumlWriter);
    }

    @Override
    protected List<String> lifeline2str(LifeLineDef lifeLine) {
        return lifeLine.getElements().stream().map(element -> element.write(lifeLine, this))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceGroup group) {
        List<String> list = new ArrayList<>();

        list.add("loop");

        list.addAll(
                group.getElements().stream().map(childElement -> childElement.write(lifeLine, this))
                        .flatMap(List::stream).collect(Collectors.toList()));

        list.add("end");

        return list;
    }

    @Override
    public List<String> write(LifeLineDef lifeLine, MessageDef message) {
        return message2str(lifeLine, message);
    }

}
