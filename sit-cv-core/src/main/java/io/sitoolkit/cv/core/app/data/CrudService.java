package io.sitoolkit.cv.core.app.data;

import io.sitoolkit.cv.core.domain.data.CrudMatrix;
import io.sitoolkit.cv.core.domain.data.CrudType;
import io.sitoolkit.cv.core.domain.data.TableDef;

public class CrudService {

    public CrudMatrix loadMatrix() {
        return generateMatrix();
    }

    public CrudMatrix generateMatrix() {

        TableDef tableA = new TableDef("TableA");
        TableDef tableB = new TableDef("TableB");
        TableDef tableC = new TableDef("TableC");

        CrudMatrix crud = new CrudMatrix();
        crud.getTableDefs().add(tableA);
        crud.getTableDefs().add(tableB);
        crud.getTableDefs().add(tableC);

        crud.add("a.b.c.AController.create(a.b.c.XEntity)", tableA, CrudType.CREATE,
                "insert into tableA values xxxx");

        crud.add("a.b.c.BController.update(java.lang.String)", tableB, CrudType.REFERENCE,
                "select * from tableB where xxxx");
        crud.add("a.b.c.BController.update(java.lang.String)", tableB, CrudType.UPDATE,
                "update tableB set xxxx");

        crud.add("a.b.c.CController.delete(java.lang.String)", tableB, CrudType.REFERENCE,
                "select * from tableB where xxxx");
        crud.add("a.b.c.CController.delete(java.lang.String)", tableC, CrudType.REFERENCE,
                "select * from tableC where xxxx");
        crud.add("a.b.c.CController.delete(java.lang.String)", tableC, CrudType.DELETE,
                "delete from tableC where xxxx");

        return crud;
    }

}
