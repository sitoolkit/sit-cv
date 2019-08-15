package io.sitoolkit.cv.core.app.crud;

import java.io.File;
import java.util.List;
import java.util.Optional;

import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import io.sitoolkit.cv.core.domain.crud.CrudMatrix;
import io.sitoolkit.cv.core.domain.crud.CrudProcessor;
import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class CrudService {

  @NonNull
  FunctionModelService functionModelService;

  @NonNull
  private CrudProcessor processor;

  @NonNull
  ProjectManager projectManager;

  public Optional<CrudMatrix> loadMatrix() {
    if (!projectManager.getCurrentProject().existsWorkDir()) {
      return Optional.empty();
    }

    Optional<CrudMatrix> crudMatrixOpt = JsonUtils
        .file2obj(projectManager.getCurrentProject().getCrudPath(), CrudMatrix.class);

    if (!crudMatrixOpt.isPresent()) {
      return generateMatrix();
    }

    CrudMatrix crudMatrix = crudMatrixOpt.get();
    File sqlLogFile = projectManager.getCurrentProject().getSqlLogPath().toFile();

    if (!sqlLogFile.exists() || crudMatrix.getSqlLogLastModified() == sqlLogFile.lastModified()) {
      return crudMatrixOpt;
    }

    return generateMatrix();
  }

  public Optional<CrudMatrix> generateMatrix() {
    Optional<List<SqlPerMethod>> sqlPerMethodList = projectManager.getSqlLog();

    if (!sqlPerMethodList.isPresent()) {
      log.warn("SQL log file not found. If you need a CRUD matrix, please run analyze-sql first.");
      return Optional.empty();
    }

    CrudMatrix methodCrud = processor.buildMatrix(sqlPerMethodList.get());

    List<ClassDef> entryPointClasses = functionModelService.getAllEntryPointClasses();

    CrudMatrix entryPointCrud = processor.adjustAxis(entryPointClasses, methodCrud);

    long lastModified = projectManager.getCurrentProject().getSqlLogPath().toFile().lastModified();
    entryPointCrud.setSqlLogLastModified(lastModified);

    JsonUtils.obj2file(entryPointCrud, projectManager.getCurrentProject().getCrudPath());

    return Optional.of(entryPointCrud);
  }

  public void analyzeSql() {
    projectManager.generateSqlLog();
  }

}
