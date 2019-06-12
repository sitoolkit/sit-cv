package io.sitoolkit.cv.core.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class FilterPatternTest {

    @Test
    public void match() {
        FilterPattern pattern = new FilterPattern(".*match");

        assertThat(pattern.match("test-match"), is(true));
        assertThat(pattern.match("not-match-test"), is(false));
    }
    
    @Test
    public void emptyTest() {
        FilterPattern pattern = new FilterPattern("");
        
        assertThat(pattern.match("no-match"), is(false));
        assertThat(pattern.match(""), is(false));
        assertThat(pattern.match(null), is(false));
    }
    
    @Test
    public void nullTest() {
        FilterPattern pattern = new FilterPattern(null);
        
        assertThat(pattern.match("no-match"), is(false));
        assertThat(pattern.match(""), is(false));
        assertThat(pattern.match(null), is(false));
    }

}
