package io.sitoolkit.cv.tools.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class FilterConditionTest {

  @Test
  public void match() {
    FilterCondition condition = new FilterCondition(".*name", "@annotation.*");

    assertThat(condition.matchName("match-name"), is(true));
    assertThat(condition.matchAnnotation("not-match-@annotation"), is(false));
  }

  @Test
  public void emptyPattern() {
    FilterCondition emptyCondition = new FilterCondition("", null);

    assertThat(emptyCondition.matchName("nomatch"), is(true));
    assertThat(emptyCondition.matchAnnotation(""), is(true));
  }
}
