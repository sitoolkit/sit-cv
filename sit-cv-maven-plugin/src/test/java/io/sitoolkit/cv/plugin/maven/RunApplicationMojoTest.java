package io.sitoolkit.cv.plugin.maven;

import java.io.File;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.resources.TestResources;
import org.junit.Rule;
import org.junit.Test;

public class RunApplicationMojoTest {

  @Rule public MojoRule mojoRule = new MojoRule();

  @Rule public TestResources resources = new TestResources();

  @Test
  public void testMojoGoal() throws Exception {
    File pom = new File(resources.getBasedir("test-project-1"), "pom.xml");

    Mojo mojo = mojoRule.lookupMojo("run", pom);
    mojo.execute();
  }
}
