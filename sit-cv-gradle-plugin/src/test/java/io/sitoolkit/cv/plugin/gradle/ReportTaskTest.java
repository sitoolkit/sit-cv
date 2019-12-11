package io.sitoolkit.cv.plugin.gradle;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class ReportTaskTest {
  @Rule public final TemporaryFolder tempDir = new TemporaryFolder();

  @Test
  public void testExport() {
    File testDir = new File("../test-project/gradle-multi");
    File projectDir = null;
    try {
      projectDir = tempDir.newFolder("report");
      FileUtils.copyDirectory(testDir, projectDir);
    } catch (IOException e) {
      e.printStackTrace();
    }

    GradleRunner.create()
        .withProjectDir(projectDir)
        .withArguments(Arrays.asList("cvReport"))
        .build();

    assertTrue(new File(projectDir, "docs").exists());
  }
}
