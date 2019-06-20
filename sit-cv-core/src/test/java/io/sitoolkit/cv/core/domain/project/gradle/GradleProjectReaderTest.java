package io.sitoolkit.cv.core.domain.project.gradle;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;

public class GradleProjectReaderTest {

    GradleProjectReader reader = new GradleProjectReader(new SqlLogProcessor());

    @Test
    public void testMultiProject() {
        Project project = reader
                .read(Paths.get("../test-project/gradle-multi").toAbsolutePath().normalize()).get();

        assertThat(project.getAllSrcDirs(),
                containsInAnyOrder(project.getDir().resolve("project-application/src/main/java"),
                        project.getDir().resolve("project-library/src/main/java")));

        Set<String> classpaths = project.getAllClasspaths().stream()
                .map(Path::toString)
                .collect(Collectors.toSet());

        assertThat(classpaths, hasItem(endsWith("commons-lang3-3.8.1.jar")));
        assertThat(classpaths, hasItem(containsString("lombok")));
    }

}
