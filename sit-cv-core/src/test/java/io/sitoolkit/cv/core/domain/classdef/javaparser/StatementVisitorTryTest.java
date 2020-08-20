package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import com.github.javaparser.ast.CompilationUnit;
import io.sitoolkit.cv.core.domain.classdef.CatchStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.FinallyStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.TryStatement;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.BeforeClass;
import org.junit.Test;

public class StatementVisitorTryTest extends StatementVisitorTest {

  static CompilationUnit compilationUnit;

  static CompilationUnit repositoryCompilationUnit;

  @BeforeClass
  public static void init() throws IOException {
    compilationUnit = parseFile("src/main/java/a/b/c/TryController.java");
    repositoryCompilationUnit = parseFile("src/main/java/a/b/c/ARepositoryFileImpl.java");
  }

  @Test
  public void tryStatement() throws IOException {
    MethodDef result = getVisitResult(testName.getMethodName());

    List<CvStatement> tryStatements =
        result.getStatements().stream()
            .filter(TryStatement.class::isInstance)
            .collect(Collectors.toList());
    assertThat(tryStatements.size(), is(1));

    TryStatement tryStatement = (TryStatement) tryStatements.get(0);
    List<CvStatement> tryChildren = tryStatement.getChildren();
    assertThat(tryChildren.size(), is(2));

    assertThat(((MethodCallDef) tryChildren.get(0)).getName(), is("read"));
    assertThat(((MethodCallDef) tryChildren.get(1)).getName(), is("process"));

    List<CatchStatement> catchStatements = tryStatement.getCatchStatements();
    assertThat(catchStatements.size(), is(2));

    assertThat(
        catchStatements.get(0).getParameter(),
        is(
            "FileNotFoundException\r\n"
                + "        | NullPointerException\r\n"
                + "        | ArrayIndexOutOfBoundsException\r\n"
                + "        | NumberFormatException"));
    assertThat(catchStatements.get(1).getParameter(), is("IOException"));

    assertThat(
        ((MethodCallDef) catchStatements.get(1).getChildren().get(0)).getName(), is("process3"));

    FinallyStatement finallyStatement = tryStatement.getFinallyStatement();
    assertThat(finallyStatement.getChildren().size(), is(1));

    assertThat(((MethodCallDef) finallyStatement.getChildren().get(0)).getName(), is("process4"));
  }

  @Test
  public void nestedTryStatement() throws IOException {
    MethodDef result = getVisitResult(testName.getMethodName());

    List<CvStatement> tryStatements =
        result.getStatements().stream()
            .filter(TryStatement.class::isInstance)
            .collect(Collectors.toList());
    assertThat(tryStatements.size(), is(1));

    TryStatement tryStatement = (TryStatement) tryStatements.get(0);
    List<CvStatement> tryChildren = tryStatement.getChildren();
    assertThat(tryChildren.size(), is(2));

    TryStatement nestedTry1 = (TryStatement) tryChildren.get(1);
    assertThat(((MethodCallDef) nestedTry1.getChildren().get(0)).getName(), is("process2"));

    List<CatchStatement> catchStatements = tryStatement.getCatchStatements();
    assertThat(catchStatements.get(0).getChildren().size(), is(1));

    TryStatement nestedTry2 = (TryStatement) catchStatements.get(0).getChildren().get(0);
    assertThat(
        ((MethodCallDef) nestedTry2.getCatchStatements().get(0).getChildren().get(0)).getName(),
        is("process3"));
    assertThat(nestedTry2.getCatchStatements().get(0).getParameter(), is("NumberFormatException"));

    FinallyStatement finallyStatement = tryStatement.getFinallyStatement();
    assertThat(finallyStatement.getChildren().size(), is(1));

    TryStatement nestedTry3 = (TryStatement) finallyStatement.getChildren().get(0);
    assertThat(
        ((MethodCallDef) nestedTry3.getCatchStatements().get(0).getChildren().get(0)).getName(),
        is("process4"));
  }

  @Test
  public void tryWithResourceStatement() throws IOException {
    MethodDef result = getVisitResult(repositoryCompilationUnit, "ARepositoryFileImpl", "save");

    List<CvStatement> tryStatements =
        result.getStatements().stream()
            .filter(TryStatement.class::isInstance)
            .collect(Collectors.toList());
    assertThat(tryStatements.size(), is(1));

    TryStatement tryStatement = (TryStatement) tryStatements.get(0);
    List<CvStatement> tryChildren = tryStatement.getChildren();
    assertThat(tryChildren.size(), is(3));

    assertThat(((MethodCallDef) tryChildren.get(0)).getName(), is("createWriter"));
    assertThat(((MethodCallDef) tryChildren.get(1)).getName(), is("obj2str"));
    assertThat(((MethodCallDef) tryChildren.get(2)).getName(), is("write"));
  }

  public MethodDef getVisitResult(String method) throws IOException {
    return getVisitResult(compilationUnit, "TryController", method);
  }
}
