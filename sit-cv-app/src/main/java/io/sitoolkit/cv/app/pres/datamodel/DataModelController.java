package io.sitoolkit.cv.app.pres.datamodel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;

@RestController
public class DataModelController {

  @Autowired
  CrudService crudService;

  @Autowired
  DataModelProcessor processor;

  @RequestMapping("/designdoc/data/crud")
  public CrudMatrix crud() {
    return crudService.loadMatrix();
  }

  @RequestMapping("/designdoc/data/crud2")
  public CrudResponseDto crud2() {
    CrudMatrix crud = crudService.loadMatrix();
    return processor.entity2dto(crud);
  }
}
