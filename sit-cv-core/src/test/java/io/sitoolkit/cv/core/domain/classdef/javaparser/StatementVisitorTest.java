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

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.project.Project;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatementVisitorTest {

    static CompilationUnit loopCompilationUnit;

    static CompilationUnit branchCompilationUnit;

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

        loopCompilationUnit = JavaParser
                .parse(projectDir.resolve("src/main/java/a/b/c/LoopController.java"));

        branchCompilationUnit = JavaParser
                .parse(projectDir.resolve("src/main/java/a/b/c/BranchController.java"));
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

    @Test
    public void ifStatement() throws IOException {
        testBranch(testName.getMethodName());
    }

    public void testFlatLoop(String method) {
        List<CvStatement> statements = new ArrayList<>();
        MethodDef methodDef = new MethodDef();

        loopCompilationUnit.getClassByName("LoopController").ifPresent(clazz -> {

            clazz.getMethodsByName(method).get(0).accept(statementVisitor, VisitContext.statementsOf(methodDef));

        });

        List<CvStatement> loopStatements = methodDef.getStatements().stream()
                .filter(LoopStatement.class::isInstance).collect(Collectors.toList());

        assertThat(loopStatements.size(), is(1));

        List<CvStatement> statementsInLoop = ((CvStatementDefaultImpl) loopStatements.get(0))
                .getChildren();

        assertThat(statementsInLoop.size(), is(1));

        MethodCallDef methodCall = (MethodCallDef) statementsInLoop.get(0);
        assertThat(methodCall.getName(), is("process"));
    }

    public void testBranch(String method) throws IOException {
        MethodDef methodDef = new MethodDef();

        branchCompilationUnit.getClassByName("BranchController").ifPresent(clazz -> {

            clazz.getMethodsByName(method).get(0).accept(statementVisitor,
                    VisitContext.statementsOf(methodDef));

        });

        List<CvStatement> branchStatements = methodDef.getStatements().stream()
                .filter(BranchStatement.class::isInstance).collect(Collectors.toList());
        assertThat(branchStatements.size(), is(1));

        List<ConditionalStatement> conditionalStatements = ((BranchStatement) branchStatements
                .get(0)).getConditions();
        assertThat(conditionalStatements.size(), is(3));

        ConditionalStatement conditionalStatement = conditionalStatements.get(0);
        assertThat(conditionalStatement.getCondition(), is("num == 0 || isTrue()"));

        MethodCallDef methodCall = (MethodCallDef) conditionalStatement.getChildren().get(0);
        assertThat(methodCall.getName(), is("process"));

        List<CvStatement> nestedStatements = conditionalStatements.get(2).getChildren().stream()
                .filter(BranchStatement.class::isInstance).collect(Collectors.toList());
        assertThat(nestedStatements.size(), is(1));

    }
}
