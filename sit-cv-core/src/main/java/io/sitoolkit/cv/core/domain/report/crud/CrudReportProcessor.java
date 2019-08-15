package io.sitoolkit.cv.core.domain.report.crud;

import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.report.Report;

public class CrudReportProcessor {

  // TODO to be refactored.
  DataModelProcessor processor = new DataModelProcessor();

  public Report<?> process(CrudMatrix crudMatrix) {
    CrudResponseDto dto = processor.entity2dto(crudMatrix);
    return Report.builder().path("datamodel/crud/crud.js").content(dto).build();
  }
}
