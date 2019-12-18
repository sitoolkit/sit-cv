package io.sitoolkit.cv.tools.infra.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Test;

public class ExceptionUtilsTest {

  @Test
  public void testExtractStackTrace() {
    Exception e = new Exception();
    List<String> expectedStackTrace =
        Stream.of(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    expectedStackTrace.add(0, e.getClass().getCanonicalName());

    List<String> actualStackTrace =
        Arrays.asList(ExceptionUtils.extractStackTrace(e).split(System.lineSeparator()));

    assertThat(actualStackTrace.size(), is(expectedStackTrace.size()));
    IntStream.of(0, actualStackTrace.size() - 1)
        .forEach(
            i ->
                assertThat(actualStackTrace.get(i), is(containsString(expectedStackTrace.get(i)))));
  }
}
