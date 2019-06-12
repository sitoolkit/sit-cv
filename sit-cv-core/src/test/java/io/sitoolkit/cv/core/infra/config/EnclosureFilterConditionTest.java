package io.sitoolkit.cv.core.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class EnclosureFilterConditionTest {

    @Test
    public void matchTest() {
        EnclosureFilterCondition condition = new EnclosureFilterCondition(".*start", "end.*");
        
        assertThat(condition.matchStart("match-start"), is(true));
        assertThat(condition.matchEnd("not-match-end"), is(false));
    }
    
    @Test
    public void emptyPatternTest() {
        EnclosureFilterCondition emptyCondition = new EnclosureFilterCondition("", null);
        
        assertThat(emptyCondition.matchStart("nomatch"), is(false));
        assertThat(emptyCondition.matchEnd(""), is(false));
    }
    
}
