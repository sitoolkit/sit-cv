package io.sitoolkit.cv.core.domain.uml;

import io.sitoolkit.cv.core.domain.functionmodel.Diagram;

public interface DiagramWriter<T extends DiagramModel> {

  Diagram write(T diagram);
}
