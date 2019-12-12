package io.sitoolkit.cv.core.infra.config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class EnclosureFilterConditionTest {

  @Test
  public void match() {
    EnclosureFilterCondition condition =
        new EnclosureFilterCondition(".*start", "end.*", ".*ignore.*", false);

    assertThat(condition.matchStart("match-start"), is(true));
    assertThat(condition.matchEnd("not-match-end"), is(false));
    assertThat(condition.matchIgnore("--- ignore line ---"), is(true));
  }

  @Test
  public void emptyPattern() {
    EnclosureFilterCondition emptyCondition = new EnclosureFilterCondition("", null, null, false);

    assertThat(emptyCondition.matchStart("nomatch"), is(false));
    assertThat(emptyCondition.matchEnd(""), is(false));
  }

  @Test
  public void substringAfterStartTest() {
    EnclosureFilterCondition condition =
        new EnclosureFilterCondition(".*Sql start : ", null, null, true);
    assertThat(
        condition.substringAfterStart(
            "1999/01/01 12:34:56.999 [main] a.b.c.Repo.method1 - Sql start : select1"),
        is("select1"));
  }
}
