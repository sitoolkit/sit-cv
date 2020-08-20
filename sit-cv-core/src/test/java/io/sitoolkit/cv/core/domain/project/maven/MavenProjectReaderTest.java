package io.sitoolkit.cv.core.domain.project.maven;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.domain.project.analyze.SqlLogProcessor;
import java.lang.Runtime.Version;
import java.nio.file.Paths;
import org.junit.Test;

public class MavenProjectReaderTest {

  MavenProjectReader reader = new MavenProjectReader(new SqlLogProcessor());

  @Test
  public void testMulti() {
    // TODO we need to test using test-project/maven-multi
    Project project = reader.read(Paths.get("..")).get();

    assertThat(
        project.getAllSrcDirs(),
        containsInAnyOrder(
            project.getDir().resolve("sit-cv-app/src/main/java"),
            project.getDir().resolve("sit-cv-core/src/main/java"),
            project.getDir().resolve("sit-cv-maven-plugin/src/main/java"),
            project.getDir().resolve("sit-cv-maven-plugin/target/generated-sources/plugin"),
            project.getDir().resolve("sit-cv-tools/src/main/java")));

    System.out.println(project.getAllClasspaths());

    Version javaVersion = Version.parse(project.getJavaVersion());
    assertThat(javaVersion.feature(), is(Runtime.version().feature()));
  }

  public static void main(String[] args) {
    System.out.println(Runtime.version());
    System.out.println(Runtime.version().feature());
  }
}
