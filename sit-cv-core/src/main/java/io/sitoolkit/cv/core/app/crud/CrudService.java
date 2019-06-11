package io.sitoolkit.cv.core.app.crud;

import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CrudService {

    @NonNull
    SitCvConfig sitCvConfig;
    
    @NonNull
    FunctionModelService functionModelService;

    @NonNull
    private CrudProcessor processor;

    @NonNull
    ProjectManager projectManager;

    public CrudMatrix loadMatrix() {
        Optional<CrudMatrix> crudMatrix = JsonUtils
                .file2obj(projectManager.getCurrentProject().getCrudPath(), CrudMatrix.class);

        return crudMatrix.orElseGet(() -> generateMatrix());
    }

    public CrudMatrix generateMatrix() {
        List<SqlPerMethod> sqlPerMethodList = projectManager.getSqlLog();

        CrudMatrix methodCrud = processor.buildMatrix(sqlPerMethodList);

        List<ClassDef> entryPointClasses = functionModelService.getAllEntryPointClasses();

        CrudMatrix entryPointCrud = processor.adjustAxis(entryPointClasses, methodCrud);

        JsonUtils.obj2file(entryPointCrud, projectManager.getCurrentProject().getCrudPath());

        return entryPointCrud;
    }

    public void analyzeSql() {
        projectManager.generateSqlLog(sitCvConfig.getSourceUrl());
    }

}
