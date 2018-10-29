package io.sitoolkit.cv.core.domain.uml;

import java.util.List;

public interface SequenceElementWriter {

    List<String> write(LifeLineDef lifeLine, SequenceGroup group);

    List<String> write(LifeLineDef lifeLine, MessageDef message);
}
