package io.sitoolkit.cv.core.infra.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

public class CvConfigServiceTest {

  CvConfigService service;

  Path mergeConfigFile;

  Path overrideConfigFile;

  @Before
  public void init() throws URISyntaxException {
    service = new CvConfigService();

    mergeConfigFile =
        Paths.get(getClass().getResource("merge/" + CvConfigReader.CONFIG_FILE_NAME).toURI());

    overrideConfigFile =
        Paths.get(getClass().getResource("override/" + CvConfigReader.CONFIG_FILE_NAME).toURI());
  }

  @Test
  public void testDefaultConfig() {
    CvConfig config = service.read(Paths.get("no/such/directory"), false);

    assertThat(config.getAsyncAnnotations(), is(List.of("Async", "Asynchronous")));
  }

  @Test
  public void testProjectConfig() throws URISyntaxException {
    CvConfig config = service.read(mergeConfigFile.getParent(), false);

    assertThat(config.getAsyncAnnotations(), is(List.of("Async", "Asynchronous", "NewOne")));
    List<LifelineClasses> lifelines = config.getLifelines();
    assertThat(lifelines.size(), is(6));
    assertThat(lifelines.get(0).getName(), is(".*Controller.*"));
    assertThat(lifelines.get(5).getName(), is("NewName"));
  }

  @Test
  public void testProjectConfigOverride() throws URISyntaxException {
    CvConfig config = service.read(overrideConfigFile.getParent(), false);

    assertThat(config.getAsyncAnnotations(), is(List.of("NewOne")));
    assertThat(config.getEntryPointFilter().getInclude().isEmpty(), is(true));
  }

  @Test
  public void testProjectConfigWatch() throws URISyntaxException, IOException {
    Path copyConfigFile = Paths.get("target", CvConfigReader.CONFIG_FILE_NAME);
    Files.copy(overrideConfigFile, copyConfigFile);
    copyConfigFile.toFile().deleteOnExit();

    CvConfig config = service.read(copyConfigFile.getParent(), true);

    assertThat(config.getAsyncAnnotations(), is(List.of("NewOne")));

    String newConfigStr = Files.readString(copyConfigFile).replace("NewOne", "AnotherOne");
    Files.writeString(copyConfigFile, newConfigStr);

    Awaitility.await()
        .atMost(Duration.ofSeconds(3))
        .until(() -> config.getAsyncAnnotations().equals((List.of("AnotherOne"))));
  }
}
