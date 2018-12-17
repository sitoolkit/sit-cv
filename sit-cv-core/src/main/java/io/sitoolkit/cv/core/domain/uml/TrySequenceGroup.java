package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrySequenceGroup extends SequenceGroup {

    private List<CatchSequenceGroup> catchGroups = new ArrayList<>();
    private SequenceGroup finallyGroup;

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }

    @Override
    public Stream<MessageDef> getMessagesRecursively() {
        Stream<MessageDef> tryMessages = Stream
                .concat(getCatchGroups().stream(), Stream.of(finallyGroup)).filter(Objects::nonNull)
                .flatMap(SequenceElement::getMessagesRecursively)
                .flatMap(SequenceElement::getMessagesRecursively);
        return Stream.concat(super.getMessagesRecursively(), tryMessages).filter(Objects::nonNull)
                .distinct();
    }

}
