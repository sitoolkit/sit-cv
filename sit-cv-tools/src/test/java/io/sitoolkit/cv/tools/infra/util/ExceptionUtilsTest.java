package io.sitoolkit.cv.tools.infra.util;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

public class ExceptionUtilsTest {

  @Test
  public void testExtractStackTrace() {
    Exception e = new Exception();
    List<String> expectedStackTrace =
        Arrays.asList(
            "java.lang.Exception",
            "io.sitoolkit.cv.tools.infra.util.ExceptionUtilsTest.testExtractStackTrace(",
            "java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(",
            "java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(",
            "java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(",
            "java.base/java.lang.reflect.Method.invoke(",
            "org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(",
            "org.junit.internal.runners.model.ReflectiveCallable.run(",
            "org.junit.runners.model.FrameworkMethod.invokeExplosively(",
            "org.junit.internal.runners.statements.InvokeMethod.evaluate(",
            "org.junit.runners.ParentRunner.runLeaf(",
            "org.junit.runners.BlockJUnit4ClassRunner.runChild(",
            "org.junit.runners.BlockJUnit4ClassRunner.runChild(",
            "org.junit.runners.ParentRunner$3.run(",
            "org.junit.runners.ParentRunner$1.schedule(",
            "org.junit.runners.ParentRunner.runChildren(",
            "org.junit.runners.ParentRunner.access$000(",
            "org.junit.runners.ParentRunner$2.evaluate(",
            "org.junit.runners.ParentRunner.run(",
            "org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(",
            "org.eclipse.jdt.internal.junit.runner.TestExecution.run(",
            "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(",
            "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(",
            "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(",
            "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(");

    List<String> actualStackTrace =
        Arrays.asList(ExceptionUtils.extractStackTrace(e).split(System.lineSeparator()));

    assertThat(actualStackTrace.size(), is(expectedStackTrace.size()));
    IntStream.of(0, actualStackTrace.size() - 1)
        .forEach(
            i ->
                assertThat(actualStackTrace.get(i), is(containsString(expectedStackTrace.get(i)))));
  }
}
