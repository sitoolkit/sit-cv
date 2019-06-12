package io.sitoolkit.cv.tools.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class FilterPatternTest {

    @Test
    public void match() {
        FilterPattern pattern = new FilterPattern(".*match", true);

        assertThat(pattern.isEmpty(), is(false));
        assertThat(pattern.match("test-match"), is(true));
        assertThat(pattern.match("not-match-test"), is(false));
    }

    @Test
    public void emptyPatternTest() {
        FilterPattern pattern = new FilterPattern("", true);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("match"), is(true));
        assertThat(pattern.match(""), is(true));
        assertThat(pattern.match(null), is(true));

        pattern = new FilterPattern("", false);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("no-match"), is(false));
        assertThat(pattern.match(""), is(false));
        assertThat(pattern.match(null), is(false));
    }

    @Test
    public void nullPatternTest() {
        FilterPattern pattern = new FilterPattern(null, true);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("no-match"), is(true));
        assertThat(pattern.match(""), is(true));
        assertThat(pattern.match(null), is(true));

        pattern = new FilterPattern(null, false);

        assertThat(pattern.isEmpty(), is(true));
        assertThat(pattern.match("no-match"), is(false));
        assertThat(pattern.match(""), is(false));
        assertThat(pattern.match(null), is(false));
    }

}
