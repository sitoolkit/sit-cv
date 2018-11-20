package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

public interface SequenceElementWriter {

    List<String> write(LifeLineDef lifeLine, LoopSequenceGroup group);

    List<String> write(LifeLineDef lifeLine, ConditionalSequenceGroup group);

    List<String> write(LifeLineDef lifeLine, BranchSequenceElement group);

    List<String> write(LifeLineDef lifeLine, MessageDef message);
}
