package io.sitoolkit.cv.core.domain.project.maven;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import java.nio.file.Paths;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.project.Project;

public class MavenProjectReaderTest {

    MavenProjectReader reader = new MavenProjectReader();

    @Test
    public void testMulti() {
        // TODO we need to test using test-project/maven-multi
        Project project = reader.read(Paths.get("..")).get();

        assertThat(project.getSrcDirs(),
                containsInAnyOrder(project.getDir().resolve("sit-cv-app/src/main/java").toString(),
                        project.getDir().resolve("sit-cv-core/src/main/java").toString()));

        System.out.println(project.getClasspaths());
    }

}
