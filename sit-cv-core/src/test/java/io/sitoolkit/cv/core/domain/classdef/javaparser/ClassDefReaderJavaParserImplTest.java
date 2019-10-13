package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import io.sitoolkit.cv.core.domain.classdef.ClassDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDefReaderJavaParserImplTest {

  @Test
  public void testProcessingTime() {

    ClassDefReaderJavaParserImpl reader = readerForSitCv();

    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    // StatementVisitor.java takes the longest processing time.
    reader.readJava(Paths.get(
        "src/main/java/io/sitoolkit/cv/core/domain/classdef/javaparser/StatementVisitor.java"));

    log.info("Proccessing time of readJava: {}", stopWatch);

    // TODO The target time is wanted to be less than 5 sec...
    long targetTime = 20L;

    assertThat("Exceeded target time", stopWatch.getTime(TimeUnit.SECONDS),
        not(greaterThan(targetTime)));
  }

  @Test
  public void testAsync() {
    ClassDef asyncService = readerForSample()
        .readJava(Paths.get("../sample/src/main/java/a/b/c/AsyncService.java")).orElseThrow();

    assertThat("this method is expected to be async",
        asyncService.findMethodBySignature("asyncWithoutResult(int)").orElseThrow().isAsync(),
        is(true));
    assertThat("this method is expected to be async",
        asyncService.findMethodBySignature("asyncWithResult(int)").orElseThrow().isAsync(),
        is(true));
  }

  private ClassDefReaderJavaParserImpl readerForSitCv() {
    return ClassDefReaderJavaParserImplFactory.create(".");
  }

  private ClassDefReaderJavaParserImpl readerForSample() {
    return ClassDefReaderJavaParserImplFactory.create("../sample");
  }
}
