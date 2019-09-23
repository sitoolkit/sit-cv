package io.sitoolkit.cv.app.pres.project;

import java.nio.file.Path;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.sitoolkit.cv.app.pres.functionmodel.FunctionModelEventListener;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.watcher.FileWatchEventListener;
import io.sitoolkit.cv.core.infra.watcher.FileWatcher;

@Component
public class ConfigFileEventHandler implements FileWatchEventListener {

  @Autowired
  FunctionModelService functionModelservice;

  @Autowired
  FunctionModelEventListener functionModelEventListener;

  @Autowired
  ProjectManager projectManager;

  FileWatcher watcher = new FileWatcher();

  @PostConstruct
  public void inittialize() {
    Path configFile = projectManager.getSitCvConfig().getSourcePath();
    if (configFile != null) {
      watcher.add(configFile);
      watcher.addListener(this);
      watcher.start();
    }
  }

  @PreDestroy
  public void terminate() {
    watcher.stop();
  }

  @Override
  public void onModify(Set<Path> files) {
    functionModelservice.getEntryPoints().stream().forEach(functionModelEventListener::onModified);
  }

}
