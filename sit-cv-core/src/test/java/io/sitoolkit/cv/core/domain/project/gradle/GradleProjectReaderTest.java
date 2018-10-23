package io.sitoolkit.cv.core.domain.project.gradle;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.nio.file.Paths;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.project.Project;

public class GradleProjectReaderTest {

    GradleProjectReader reader = new GradleProjectReader();

    @Test
    public void testMultiProject() {
        Project project = reader
                .read(Paths.get("../test-project/gradle-multi").toAbsolutePath().normalize()).get();

        assertThat(project.getSrcDirs(),
                containsInAnyOrder(project.getDir().resolve("project-application/src/main/java"),
                        project.getDir().resolve("project-library/src/main/java")));

        String classpath = project.getClasspaths().iterator().next().toString();
        assertThat(classpath, endsWith(
                "org.apache.commons/commons-lang3/3.8.1/6505a72a097d9270f7a9e7bf42c4238283247755/commons-lang3-3.8.1.jar"));
    }

}
