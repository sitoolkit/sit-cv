package io.sitoolkit.cv.core.domain.uml;

import io.sitoolkit.cv.core.domain.functionmodel.Diagram;

public interface ClassDiagramWriter {

  Diagram write(ClassDiagram diagram);
}
