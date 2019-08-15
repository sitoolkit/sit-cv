package io.sitoolkit.cv.app.pres.datamodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.report.crud.CrudResponseDto;
import io.sitoolkit.cv.core.domain.report.crud.DataModelProcessor;

@RestController
public class DataModelController {

  @Autowired
  CrudService crudService;

  // TODO to be refactored.
  DataModelProcessor processor = new DataModelProcessor();

  @RequestMapping("/designdoc/data/crud")
  public CrudResponseDto crud() {
    CrudMatrix crud = crudService.loadMatrix();
    return processor.entity2dto(crud);
  }
}
