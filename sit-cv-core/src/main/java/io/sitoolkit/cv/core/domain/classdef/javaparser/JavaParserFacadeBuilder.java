package io.sitoolkit.cv.core.domain.classdef.javaparser;

import java.io.IOException;
import java.nio.file.Path;

import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess.PreProcessingProject;
import io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess.PreProcessor;
import io.sitoolkit.cv.core.domain.project.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JavaParserFacadeBuilder {

    private JavaParserFacadeBuilder() {
    }

    public static JavaParserFacade build(Project project) {
        return build(new PreProcessingProject(project, PreProcessor.DO_NOTHING));
    }

    public static JavaParserFacade build(PreProcessingProject ppProject) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());
        ppProject.getParseTargetSrcDirs().stream().forEach(
                srcDir -> combinedTypeSolver.add(new JavaParserTypeSolver(srcDir.toFile())));
        // project.getBinDirs().stream()
        // .forEach(binDir ->
        // combinedTypeSolver.add(ClassDirTypeSolver.get(binDir)));
        ppProject.getClasspaths().stream().map(Path::toAbsolutePath).map(Path::toString)
                .forEach(str -> {
                    try {
                        combinedTypeSolver.add(JarTypeSolver.getJarTypeSolver(str));
                        log.info("jar is added. {}", str);
                    } catch (IOException e) {
                        log.warn("warn ", e);
                    }
                });

        return JavaParserFacade.get(combinedTypeSolver);
    }

}
