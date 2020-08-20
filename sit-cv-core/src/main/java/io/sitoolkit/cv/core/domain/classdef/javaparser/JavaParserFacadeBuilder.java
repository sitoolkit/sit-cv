package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import io.sitoolkit.cv.core.domain.project.Project;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaParserFacadeBuilder {

  private JavaParserFacadeBuilder() {}

  public static JavaParserFacade build(Project project) {
    CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
    combinedTypeSolver.add(new ReflectionTypeSolver());
    project.getAllPreProcessedDirs().stream()
        .forEach(srcDir -> combinedTypeSolver.add(new JavaParserTypeSolver(srcDir.toFile())));
    // project.getBinDirs().stream()
    // .forEach(binDir ->
    // combinedTypeSolver.add(ClassDirTypeSolver.get(binDir)));
    Set<Path> classpaths = project.getAllClasspaths();
    log.info("Adding classpaths for JavaParser:{}", classpaths);
    classpaths.stream()
        .map(Path::toAbsolutePath)
        .map(Path::toString)
        .filter(path -> path.endsWith(".jar"))
        .forEach(
            str -> {
              try {
                combinedTypeSolver.add(JarTypeSolver.getJarTypeSolver(str));
              } catch (IOException e) {
                log.warn(e.getMessage(), e);
              }
            });

    return JavaParserFacade.get(combinedTypeSolver);
  }
}
