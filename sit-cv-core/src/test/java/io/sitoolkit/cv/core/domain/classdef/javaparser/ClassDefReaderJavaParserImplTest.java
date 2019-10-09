package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.not;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.BeforeClass;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClassDefReaderJavaParserImplTest {

  static ClassDefReaderJavaParserImpl reader;

  @BeforeClass
  public static void init() {
    reader = ClassDefReaderJavaParserImplFactory.create(".");
  }

  @Test
  public void testProcessingTime() {

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

}
