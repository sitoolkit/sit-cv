package io.sitoolkit.cv.app.pres.project;

import io.sitoolkit.cv.app.pres.designdoc.DesignDocTreeEventListener;
import io.sitoolkit.cv.app.pres.functionmodel.FunctionModelEventListener;
import io.sitoolkit.cv.core.app.functionmodel.AnalysisResult;
import io.sitoolkit.cv.core.app.functionmodel.FunctionModelService;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.infra.watcher.FileWatchEventListener;
import io.sitoolkit.cv.core.infra.watcher.FileWatcher;
import java.nio.file.Path;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceFileEventHandler implements FileWatchEventListener {

  @Autowired DesignDocTreeEventListener designDocTreeEventListener;

  @Autowired FunctionModelEventListener functionModelEventListener;

  @Autowired FunctionModelService functionModelService;

  @Autowired ProjectManager projectManager;

  FileWatcher watcher = new FileWatcher();

  @PostConstruct
  public void initialize() {
    projectManager.getCurrentProject().getAllSrcDirs().forEach(watcher::add);
    watcher.addListener(this);
    watcher.start();
  }

  @PreDestroy
  public void terminate() {
    watcher.stop();
  }

  @Override
  public void onModify(Set<Path> srcFiles) {

    AnalysisResult result = functionModelService.analyze(srcFiles);

    result.getEffectedSourceIds().forEach(functionModelEventListener::onModify);

    if (result.isEntryPointModified()) {
      designDocTreeEventListener.onModify();
    }
  }
}
