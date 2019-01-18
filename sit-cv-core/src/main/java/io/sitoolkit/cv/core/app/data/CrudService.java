package io.sitoolkit.cv.core.app.data;

import io.sitoolkit.cv.core.domain.data.CrudMatrix;
import io.sitoolkit.cv.core.domain.data.CrudRow;
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

        String functionA = "a.b.c.AController.save(a.b.c.XEntity)";
        crud.add(functionA, tableA, CrudType.CREATE, "insert into tableA values xxxx");
        CrudRow rowA = crud.getCrudRowMap().get(functionA);
        rowA.setActionPath("/a/create");
        rowA.getRepositoryFunctions().add("ARepository.create");

        String functionB = "a.b.c.BController.update(java.lang.String)";
        crud.add(functionB, tableB, CrudType.REFERENCE, "select * from tableB where xxxx");
        crud.add(functionB, tableB, CrudType.UPDATE, "update tableB set xxxx");
        CrudRow rowB = crud.getCrudRowMap().get(functionB);
        rowB.setActionPath("/b/update");
        rowB.getRepositoryFunctions().add("BRepository.find");
        rowB.getRepositoryFunctions().add("BRepository.update");

        String functionC = "a.b.c.CController.delete(java.lang.String)";
        crud.add(functionC, tableC, CrudType.REFERENCE, "select * from tableC where xxxx");
        crud.add(functionC, tableB, CrudType.REFERENCE, "select * from tableB where xxxx");
        crud.add(functionC, tableC, CrudType.DELETE, "delete from tableC where xxxx");
        CrudRow rowC = crud.getCrudRowMap().get(functionC);
        rowC.setActionPath("/c/delete");
        rowC.getRepositoryFunctions().add("CRepository.find");
        rowC.getRepositoryFunctions().add("BRepository.find");
        rowC.getRepositoryFunctions().add("CRepository.delete");

        String functionD = "a.b.c.AService.search(a.b.c.SearchCondition)";
        crud.add(functionD, tableA, CrudType.REFERENCE, "select * from tableA where xxxx");
        CrudRow rowD = crud.getCrudRowMap().get(functionD);
        rowD.setActionPath(null);
        rowD.getRepositoryFunctions().add("ARepository.search");

        return crud;
    }

}
