package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.project.ProjectManager;
import io.sitoolkit.cv.core.domain.project.ProjectReader;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import io.sitoolkit.cv.core.domain.project.maven.MavenProjectReader;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;

public class ClassDefReaderJavaParserImplTest {

  @Test
  public void test() {
    SitCvConfig config = new SitCvConfig();
    ClassDefRepository reposiotry = new ClassDefRepositoryMemImpl(config);
    List<ProjectReader> readers = Arrays.asList(new MavenProjectReader(new SqlLogProcessor()));
    ProjectManager projectManager = new ProjectManager(readers, config);

    projectManager.load(Paths.get("."));

    ClassDefReaderJavaParserImpl parser = new ClassDefReaderJavaParserImpl(reposiotry,
        projectManager, config);

    parser.init();

    parser.readJava(Paths.get(
        "/Users/yuichi_kuwahara/Documents/eclipse_workspace/sit-cv/sit-cv-core/src/main/java/io/sitoolkit/cv/core/domain/classdef/javaparser/StatementVisitor.java"));

  }
}
