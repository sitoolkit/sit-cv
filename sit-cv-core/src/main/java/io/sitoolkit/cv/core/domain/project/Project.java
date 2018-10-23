package io.sitoolkit.cv.core.domain.project;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class Project {

    @Setter(AccessLevel.NONE)
    private Path dir;

    private Set<Path> srcDirs = new HashSet<>();

    private Set<Path> classpaths = new HashSet<>();

    public Project(Path dir) {
        super();
        this.dir = dir.toAbsolutePath().normalize();
    }
}
