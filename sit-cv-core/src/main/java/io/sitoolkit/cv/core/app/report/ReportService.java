package io.sitoolkit.cv.core.app.report;

import java.util.List;

import io.sitoolkit.cv.core.app.crud.CrudService;
import io.sitoolkit.cv.core.app.designdoc.DesignDocService;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.functionmodel.FunctionModel;
import io.sitoolkit.cv.core.domain.menu.MenuItem;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.report.Report;
import io.sitoolkit.cv.core.domain.report.ReportWriter;
import io.sitoolkit.cv.core.domain.report.crud.CrudReportProcessor;
import io.sitoolkit.cv.core.domain.report.designdoc.DesignDocReportProcessor;
import io.sitoolkit.cv.core.domain.report.functionmodel.FunctionModelReportProcessor;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ReportService {

  private FunctionModelReportProcessor functionModelReportProcessor;

  private DesignDocReportProcessor designDocReportProcessor;

  private CrudReportProcessor crudReportProcessor;

  private ReportWriter reportWriter;

  private FunctionModelService functionModelService;

  private DesignDocService designDocService;

  private CrudService crudService;

  private ProjectManager projectManager;

  public void export() {
    reportWriter.initDirectory(projectManager.getCurrentProject().getDir());

    List<FunctionModel> functionModels = functionModelService.getAll();
    List<Report<?>> reports = functionModelReportProcessor.process(functionModels);

    List<MenuItem> menuList = designDocService.buildMenu();
    reports.add(designDocReportProcessor.process(menuList));

    crudService.loadMatrix().ifPresent(crudMatrix -> {
      reports.add(crudReportProcessor.process(crudMatrix));
    });

    reportWriter.write(projectManager.getCurrentProject().getDir(), reports);
  }

}
