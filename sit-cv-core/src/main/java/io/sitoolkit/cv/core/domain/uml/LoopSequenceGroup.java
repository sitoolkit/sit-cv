package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

import lombok.Data;

@Data
public class LoopSequenceGroup extends SequenceGroup {

    @Override
    public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
        return writer.write(lifeLine, this);
    }
}
