package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Data;

@Data
public class ConditionalSequenceGroup extends SequenceElement {

    private String condition;

    private boolean isStart = false;

    private List<SequenceElement> elements = new ArrayList<>();

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }

    @Override
    public Stream<MessageDef> getMessagesRecursively() {
        return getElements().stream().flatMap(SequenceElement::getMessagesRecursively)
                .filter(Objects::nonNull).distinct();
    }
}
