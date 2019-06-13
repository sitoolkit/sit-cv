package io.sitoolkit.cv.tools.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;

import org.junit.Test;

public class RepositoryLoggerArgumentParserTest {

    @Test
    public void parse() throws MalformedURLException {
        URL url = Paths.get("./src/test/resources/test-sit-cv-config.json").toUri().toURL();
        RepositoryLoggerConfig config = new RepositoryLoggerArgumentParser().parse(
                "configUrl=" + url.toString() + ";repositoryMethodMarker=[RepositoryMethod]");

        FilterConditionGroup filter = config.getRepositoryFilter();
        assertThat(filter.getInclude().size(), is(2));
        assertThat(filter.getInclude().get(1).getNamePattern().isEmpty(), is(true));
        assertThat(filter.getInclude().get(1).getAnnotationPattern().getPattern().toString(),
                is("@.*Repository"));
        assertThat(filter.getExclude().size(), is(0));
        assertThat(config.getRepositoryMethodMarker(), is("[RepositoryMethod]"));
    }

}
