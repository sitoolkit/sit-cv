package io.sitoolkit.cv.core.domain.uml;

import java.util.List;
import java.util.stream.Stream;

import lombok.Data;

@Data
public abstract class SequenceElement {

    public abstract Stream<MessageDef> getMessagesRecursively();

    public abstract List<String> write(LifeLineDef lifeline, SequenceElementWriter writer);
}
