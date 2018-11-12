package io.sitoolkit.cv.core.domain.classdef.javaparser.preprocess;

import java.nio.file.Path;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.lombok.DelombokParameter;
import io.sitoolkit.cv.core.infra.lombok.Delomboker;

public class DelombokProcessor implements PreProcessor{

    final Project original;
    final Delomboker delomboker;

    public DelombokProcessor(Project project) {
        this.original = project;
        this.delomboker = new Delomboker();
    }

    @Override
    public Path getTargetSrcPath(Path srcDir) {
        Path relativized = original.getDir().relativize(srcDir);

        //TODO Confirm delombok-ed directory's Specifications
        Path delomboked = original.getDir().resolve("_delomboked").resolve(relativized.toString());

        return delomboked;
    }

    @Override
    public void execute(Path srcDir, Path targetDir) {
        DelombokParameter param = DelombokParameter.builder()
                .src(srcDir)
                .target(targetDir)
                .encoding("UTF-8")
                .classpath(original.getClasspaths())
                .sourcepath(original.getSrcDirs())
                .build();

        delomboker.execute(param);
    }

}
