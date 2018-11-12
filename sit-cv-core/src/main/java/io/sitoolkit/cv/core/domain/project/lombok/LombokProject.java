package io.sitoolkit.cv.core.domain.project.lombok;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.lombok.DelombokParametar;
import io.sitoolkit.cv.core.infra.lombok.DelombokParametar.DelombokParametarBuilder;
import io.sitoolkit.cv.core.infra.lombok.Delomboker;

public class LombokProject extends Project {

    final Project original;
    final Delomboker delomboker;

    public LombokProject(Project project) {
        super(project.getDir());
        super.setClasspaths(project.getClasspaths());
        this.original = project;
        this.delomboker = new Delomboker();
        Set<Path> srcDirs = project.getWatchDirs().stream()
                .map(watchDir -> mapSrcPath(project.getDir(), watchDir))
                .collect(Collectors.toSet());
        super.setSrcDirs(srcDirs);
    }

    @Override
    public void refresh() {
        original.refresh();

        DelombokParametarBuilder paramBuilder = DelombokParametar.builder()
                .encoding("UTF-8")
                .classpath(getClasspaths())
                .sourcepath(original.getSrcDirs());

        original.getSrcDirs().forEach(src -> {
            DelombokParametar param = paramBuilder
                    .src(src)
                    .target(mapSrcPath(getDir(), src))
                    .build();
            delomboker.execute(param);
        });

    }

    @Override
    public Set<Path> getWatchDirs() {
        return original.getWatchDirs();
    }

    @Override
    public Path getSrcFile(Path inputFile) {
        return mapSrcPath(getDir(), inputFile);
    }

    Path mapSrcPath(Path projectDir, Path srcDir) {
        Path relativized = projectDir.relativize(srcDir);

        //TODO Confirm delombok-ed directory's Specifications
        Path delomboked = projectDir.resolve("_delomboked").resolve(relativized.toString());

        return delomboked;
    }

}
