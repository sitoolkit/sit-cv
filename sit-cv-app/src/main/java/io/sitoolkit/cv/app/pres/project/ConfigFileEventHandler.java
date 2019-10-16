package io.sitoolkit.cv.app.pres.project;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sitoolkit.cv.app.pres.designdoc.DesignDocTreeEventListener;
import io.sitoolkit.cv.app.pres.functionmodel.FunctionModelEventListener;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.config.CvConfigEventListener;

@Component
public class ConfigFileEventHandler implements CvConfigEventListener {

  @Autowired
  FunctionModelService functionModelservice;

  @Autowired
  FunctionModelEventListener functionModelEventListener;

  @Autowired
  DesignDocTreeEventListener designDocTreeEventListener;

  @Autowired
  ProjectManager projectManager;

  @PostConstruct
  public void initialize() {
    projectManager.getCvConfig().addEventListener(this);
  }

  @Override
  public void onModify() {
    functionModelservice.getEntryPoints().stream().forEach(functionModelEventListener::onModify);
    designDocTreeEventListener.onModify();
  }

}
