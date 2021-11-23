package io.sitoolkit.cv.core.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Test;

public class EnclosureFilterConditionTest {

  @Test
  public void match() {
    EnclosureFilterCondition condition = new EnclosureFilterCondition(".*start", "end.*", null);

    assertThat(condition.matchStart("match-start"), is(true));
    assertThat(condition.matchEnd("not-match-end"), is(false));
  }

  @Test
  public void emptyPattern() {
    EnclosureFilterCondition emptyCondition = new EnclosureFilterCondition("", null, null);

    assertThat(emptyCondition.matchStart("nomatch"), is(false));
    assertThat(emptyCondition.matchEnd(""), is(false));
  }

  @Test
  public void matchPattern() {
    EnclosureFilterCondition condition =
        new EnclosureFilterCondition(null, null, ".* Sql start : (.*)");
    String sqlLog = "1999/01/01 12:34:56.999 [main] a.b.c.Repo.method1 - Sql start : select1";

    assertThat(condition.matchRegex(sqlLog), is(true));
    assertThat(condition.getMatchString(sqlLog), is("select1"));
  }
}
