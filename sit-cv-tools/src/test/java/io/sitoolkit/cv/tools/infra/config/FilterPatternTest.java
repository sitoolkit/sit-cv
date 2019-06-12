package io.sitoolkit.cv.tools.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import io.sitoolkit.cv.tools.infra.config.FilterPattern;

public class FilterPatternTest {

    @Test
    public void match() {
        FilterPattern pattern = new FilterPattern(".*match", true);

        assertThat(pattern.isEmpty(), is(false));
        assertThat(pattern.match("test-match"), is(true));
        assertThat(pattern.match("not-match-test"), is(false));
    }

    @Test
    public void emptyPattern() {
        FilterPattern pattern = new FilterPattern("", true);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("match"), is(true));
        assertThat(pattern.match(""), is(true));
        assertThat(pattern.match(null), is(true));
    }

    @Test
    public void nullPattern() {
        FilterPattern pattern = new FilterPattern(null, false);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("no-match"), is(true));
        assertThat(pattern.match(""), is(true));
        assertThat(pattern.match(null), is(true));
    }

}
