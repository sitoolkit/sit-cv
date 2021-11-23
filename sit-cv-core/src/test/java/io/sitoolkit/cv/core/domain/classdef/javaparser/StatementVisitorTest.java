package io.sitoolkit.cv.core.domain.classdef.javaparser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.project.Project;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class StatementVisitorTest {

  static Path projectDir;

  static StatementVisitor statementVisitor;

  // @Rule public TestName testName = new TestName();

  private TestInfo testInfo;

  @BeforeEach
  void init(TestInfo testInfo) {
    this.testInfo = testInfo;
  }

  protected String getTestMethodName() {
    return testInfo.getDisplayName().replace("()", "");
  }

  @BeforeAll
  public static void initVisitor() throws IOException {
    projectDir = Paths.get("../sample");
    Path srcDir = projectDir.resolve("src/main/java");

    Project project = new Project(projectDir);
    project.getSrcDirs().addAll(Arrays.asList(srcDir));

    statementVisitor = StatementVisitor.build(JavaParserFacadeBuilder.build(project));
  }

  public static CompilationUnit parseFile(String path) throws IOException {
    return JavaParser.parse(projectDir.resolve(path));
  }

  public static MethodDef getVisitResult(
      CompilationUnit compilationUnit, String className, String method) throws IOException {
    MethodDef methodDef = new MethodDef();

    compilationUnit
        .getClassByName(className)
        .ifPresent(
            clazz -> {
              clazz
                  .getMethodsByName(method)
                  .get(0)
                  .accept(statementVisitor, VisitContext.of(methodDef));
            });

    return methodDef;
  }
}
