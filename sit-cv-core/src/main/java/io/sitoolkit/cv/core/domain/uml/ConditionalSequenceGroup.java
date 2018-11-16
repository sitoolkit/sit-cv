package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

import lombok.Data;

@Data
public class ConditionalSequenceGroup extends SequenceGroup {

    private String condition;
    private boolean isFirst = false;

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }
}
