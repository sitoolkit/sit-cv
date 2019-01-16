package io.sitoolkit.cv.core.domain.uml;

import io.sitoolkit.cv.core.domain.function.Diagram;

public interface DiagramWriter<T extends DiagramModel> {

    Diagram write(T diagram);
}
