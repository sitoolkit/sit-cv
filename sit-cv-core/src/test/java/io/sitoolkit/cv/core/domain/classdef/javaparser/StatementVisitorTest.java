package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.project.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatementVisitorTest {

    static CompilationUnit compilationUnit;

    static StatementVisitor statementVisitor;

    @Rule
    public TestName testName = new TestName();

    @BeforeClass
    public static void init() throws IOException {
        Path projectDir = Paths.get("../sample");
        Path srcDir = projectDir.resolve("src/main/java");

        Project project = new Project(projectDir);
        project.getSrcDirs().addAll(Arrays.asList(srcDir));

        statementVisitor = StatementVisitor.build(JavaParserFacadeBuilder.build(project));

        compilationUnit = JavaParser
                .parse(projectDir.resolve("src/main/java/a/b/c/LoopController.java"));
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

    public void testFlatLoop(String method) {
        List<CvStatement> statements = new ArrayList<>();

        compilationUnit.getClassByName("LoopController").ifPresent(clazz -> {

            clazz.getMethodsByName(method).get(0).accept(statementVisitor, statements);

        });

        List<CvStatement> loopStatements = statements.stream()
                .filter(LoopStatement.class::isInstance).collect(Collectors.toList());

        assertThat(loopStatements.size(), is(1));

        List<CvStatement> statementsInLoop = loopStatements.get(0).getChildren();

        assertThat(statementsInLoop.size(), is(1));

        MethodCallDef methodCall = (MethodCallDef) statementsInLoop.get(0);
        assertThat(methodCall.getName(), is("process"));
    }

}
