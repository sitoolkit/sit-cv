package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.domain.project.maven.MavenProjectReader;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.config.SitCvConfigReader;

public class ClassDefReaderJavaParserImplFactory {

  private static Map<String, ClassDefReaderJavaParserImpl> cache = new HashMap<>();

  static ClassDefReaderJavaParserImpl create(String projectPath) {
    return cache.computeIfAbsent(projectPath, pjPath -> createWithoutCache(pjPath));
  }

  private static ClassDefReaderJavaParserImpl createWithoutCache(String projectPath) {

    Path projectPathObj = Paths.get(projectPath);

    SitCvConfigReader configReader = new SitCvConfigReader();
    SitCvConfig config = configReader.read(projectPathObj, false);

    ClassDefRepository reposiotry = new ClassDefRepositoryMemImpl(config);
    List<ProjectReader> readers = Arrays.asList(new MavenProjectReader(new SqlLogProcessor()));
    ProjectManager projectManager = new ProjectManager(readers, config);

    projectManager.load(projectPathObj);

    ClassDefReaderJavaParserImpl reader = new ClassDefReaderJavaParserImpl(reposiotry,
        projectManager, config);

    reader.init();

    return reader;
  }

}
