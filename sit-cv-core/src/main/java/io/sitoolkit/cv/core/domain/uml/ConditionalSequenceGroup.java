package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ConditionalSequenceGroup extends SequenceGroup {

    private String condition;
    private int order;

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }
}
