package io.sitoolkit.cv.tools.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.MalformedURLException;

import org.junit.Test;

public class RepositoryLoggerArgumentParserTest {

  String base = "repositoryMethodMarker=[RepositoryMethod];" + "projectType=maven;";

  String filter1 =
      "include.repositoryFilter1.annotation=.*(Repository|Named);"
          + "include.repositoryFilter1.name=.*Repository.*;";

  String filter2 =
      "include.repositoryFilter2.annotation=;"
          + "include.repositoryFilter2.name=aaaaa;"
          + "include.repositoryFilter3.annotation=bbbbb;"
          + "include.repositoryFilter3.name=;";

  String filter3 =
      "exclude.repositoryFilter1.annotation=;"
          + "exclude.repositoryFilter1.name=ccccc;"
          + "exclude.repositoryFilter2.annotation=ddddd;"
          + "exclude.repositoryFilter2.name=;";

  @Test
  public void testParseBasic() throws MalformedURLException {
    RepositoryLoggerConfig config = new RepositoryLoggerArgumentParser().parse(filter1 + base);

    FilterConditionGroup filter = config.getRepositoryFilter();
    assertThat(filter.getInclude().size(), is(1));
    assertThat(
        filter.getInclude().get(0).getNamePattern().getPattern().toString(), is(".*Repository.*"));
    assertThat(
        filter.getInclude().get(0).getAnnotationPattern().getPattern().toString(),
        is(".*(Repository|Named)"));
    assertThat(filter.getExclude().size(), is(0));
    assertThat(config.getRepositoryMethodMarker(), is("[RepositoryMethod]"));
    assertThat(config.getProjectType(), is("maven"));
  }

  @Test
  public void testParseNoFilter() throws MalformedURLException {
    RepositoryLoggerConfig config = new RepositoryLoggerArgumentParser().parse(base);

    FilterConditionGroup filter = config.getRepositoryFilter();
    assertThat(filter.getInclude().size(), is(0));
    assertThat(filter.getExclude().size(), is(0));
    assertThat(config.getRepositoryMethodMarker(), is("[RepositoryMethod]"));
  }

  @Test
  public void testParseManyFilter() throws MalformedURLException {
    RepositoryLoggerConfig config =
        new RepositoryLoggerArgumentParser().parse(filter1 + filter2 + filter3 + base);

    FilterConditionGroup filter = config.getRepositoryFilter();
    assertThat(filter.getInclude().size(), is(3));
    assertThat(
        filter.getInclude().get(0).getNamePattern().getPattern().toString(), is(".*Repository.*"));
    assertThat(
        filter.getInclude().get(0).getAnnotationPattern().getPattern().toString(),
        is(".*(Repository|Named)"));
    assertThat(filter.getInclude().get(1).getNamePattern().getPattern().toString(), is("aaaaa"));
    assertThat(filter.getInclude().get(1).getAnnotationPattern().isEmpty(), is(true));
    assertThat(filter.getInclude().get(2).getNamePattern().isEmpty(), is(true));
    assertThat(
        filter.getInclude().get(2).getAnnotationPattern().getPattern().toString(), is("bbbbb"));
    assertThat(filter.getExclude().size(), is(2));
    assertThat(filter.getExclude().get(0).getNamePattern().getPattern().toString(), is("ccccc"));
    assertThat(filter.getExclude().get(0).getAnnotationPattern().isEmpty(), is(true));
    assertThat(filter.getExclude().get(1).getNamePattern().isEmpty(), is(true));
    assertThat(
        filter.getExclude().get(1).getAnnotationPattern().getPattern().toString(), is("ddddd"));
    assertThat(config.getRepositoryMethodMarker(), is("[RepositoryMethod]"));
  }
}
