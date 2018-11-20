package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LoopSequenceGroup extends SequenceGroup {

    private String condition;

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }
}
