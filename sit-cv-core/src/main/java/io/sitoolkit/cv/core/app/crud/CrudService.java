package io.sitoolkit.cv.core.app.crud;

import java.util.List;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrudService {

    @NonNull
    FunctionModelService functionModelService;

    @NonNull
    private CrudProcessor processor;

    @NonNull
    ProjectManager projectManager;

    public CrudMatrix loadMatrix() {
        return generateMatrix();
    }

    public CrudMatrix generateMatrix() {
        List<SqlPerMethod> sqlPerMethodList = projectManager.getSqlLog();

        CrudMatrix methodCrud = processor.buildMatrix(sqlPerMethodList);

        List<ClassDef> entryPointClasses = functionModelService.getAllEntryPointClasses();

        CrudMatrix entryPointCrud = processor.adjustAxis(entryPointClasses, methodCrud);

        return entryPointCrud;
    }

}
