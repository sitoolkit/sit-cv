package io.sitoolkit.cv.core.app.crud;

import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.CrudReader;
import io.sitoolkit.cv.core.domain.crud.CrudWriter;
import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
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

    @NonNull
    private CrudReader reader;

    @NonNull
    private CrudWriter writer;

    @NonNull
    private SitCvConfig config;

    public CrudMatrix loadMatrix() {
        Optional<CrudMatrix> crudMatrix = reader
                .read(projectManager.getCurrentProject().getCrudPath());

        return crudMatrix.orElseGet(() -> generateMatrix());
    }

    public CrudMatrix generateMatrix() {
        List<SqlPerMethod> sqlPerMethodList = projectManager.getSqlLog();

        CrudMatrix methodCrud = processor.buildMatrix(sqlPerMethodList);

        List<ClassDef> entryPointClasses = functionModelService.getAllEntryPointClasses();

        CrudMatrix entryPointCrud = processor.adjustAxis(entryPointClasses, methodCrud);

        writer.write(entryPointCrud, projectManager.getCurrentProject().getCrudPath());

        return entryPointCrud;
    }

    public void analyzeSql() {
        projectManager.generateSqlLog();
    }

}
