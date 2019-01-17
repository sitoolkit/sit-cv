package io.sitoolkit.cv.app.pres.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.sitoolkit.cv.core.app.data.CrudService;
import io.sitoolkit.cv.core.domain.data.CrudMatrix;

@RestController
public class DataModelController {

    @Autowired
    CrudService crudService;

    @RequestMapping("/designdoc/data/crud")
    public CrudMatrix crud() {
        return crudService.loadMatrix();
    }
}
