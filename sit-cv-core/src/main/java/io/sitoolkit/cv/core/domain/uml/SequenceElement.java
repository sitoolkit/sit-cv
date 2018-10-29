package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

import lombok.Data;

@Data
public abstract class SequenceElement {

    public abstract List<String> write(LifeLineDef lifeline, SequenceElementWriter writer);
}
