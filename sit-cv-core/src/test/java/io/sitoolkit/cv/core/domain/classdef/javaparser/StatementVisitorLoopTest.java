package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;

public class StatementVisitorLoopTest extends StatementVisitorTest {

  static CompilationUnit compilationUnit;

  @BeforeClass
  public static void init() throws IOException {
    compilationUnit = parseFile("src/main/java/a/b/c/LoopController.java");
  }

  @Test
  public void simpleFor() throws IOException {
    testFlatLoop(testName.getMethodName());
  }

  @Test
  public void forEach() throws IOException {
    testFlatLoop(testName.getMethodName());
  }

  @Test
  public void streamMethodRef() throws IOException {
    testFlatLoop(testName.getMethodName());
  }

  @Test
  public void streamLambda() throws IOException {
    testFlatLoop(testName.getMethodName());
  }

  public void testFlatLoop(String method) throws IOException {
    MethodDef methodDef = getVisitResult(compilationUnit, "LoopController", method);

    List<CvStatement> loopStatements =
        methodDef
            .getStatements()
            .stream()
            .filter(LoopStatement.class::isInstance)
            .collect(Collectors.toList());

    assertThat(loopStatements.size(), is(1));

    List<CvStatement> statementsInLoop =
        ((CvStatementDefaultImpl) loopStatements.get(0)).getChildren();

    assertThat(statementsInLoop.size(), is(1));

    MethodCallDef methodCall = (MethodCallDef) statementsInLoop.get(0);
    assertThat(methodCall.getName(), is("process"));
  }
}
