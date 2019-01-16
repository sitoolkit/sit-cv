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

        assertThat(project.getAllSrcDirs(),
                containsInAnyOrder(project.getDir().resolve("sit-cv-app/src/main/java"),
                        project.getDir().resolve("sit-cv-core/src/main/java"),
                        project.getDir().resolve("sit-cv-maven-plugin/src/main/java"),
                        project.getDir()
                                .resolve("sit-cv-maven-plugin/target/generated-sources/plugin"),
                        project.getDir().resolve("sit-cv-tools/src/main/java")));

        System.out.println(project.getAllClasspaths());
    }

}
