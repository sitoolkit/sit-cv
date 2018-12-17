package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import io.sitoolkit.cv.core.domain.classdef.CatchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatementDefaultImpl;
import io.sitoolkit.cv.core.domain.classdef.FinallyStatement;
import io.sitoolkit.cv.core.domain.classdef.LoopStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.TryStatement;
import io.sitoolkit.cv.core.domain.project.Project;
import javassist.bytecode.stackmap.BasicBlock.Catch;
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
        MethodDef result = getBranchVisitResult(testName.getMethodName());

        List<CvStatement> branchStatements = result.getStatements().stream()
                .filter(BranchStatement.class::isInstance).collect(Collectors.toList());
        assertThat(branchStatements.size(), is(1));

        List<ConditionalStatement> conditionalStatements = ((BranchStatement) branchStatements
                .get(0)).getConditions();
        assertThat(conditionalStatements.size(), is(3));

        assertThat(conditionalStatements.get(0).isFirst(), is(true));
        assertThat(conditionalStatements.get(1).isFirst(), is(false));
        assertThat(conditionalStatements.get(2).isFirst(), is(false));

        ConditionalStatement conditionalStatement = conditionalStatements.get(0);
        assertThat(conditionalStatement.getCondition(), is("(num == 0 || (isTrue()))"));

        MethodCallDef methodCall = (MethodCallDef) conditionalStatement.getChildren().get(0);
        assertThat(methodCall.getName(), is("process"));
    }

    @Test
    public void nestedIfStatement() throws IOException {
        MethodDef result = getBranchVisitResult(testName.getMethodName());

        List<ConditionalStatement> conditionalStatements = result.getStatements().stream()
                .filter(BranchStatement.class::isInstance).map(BranchStatement.class::cast)
                .collect(Collectors.toList()).get(0).getConditions();

        List<BranchStatement> nestedBranches = conditionalStatements.get(2).getChildren().stream()
                .filter(BranchStatement.class::isInstance).map(BranchStatement.class::cast)
                .collect(Collectors.toList());
        assertThat(nestedBranches.size(), is(1));

        List<ConditionalStatement> nestedConditions = nestedBranches.get(0).getConditions();
        assertThat(nestedConditions.size(), is(3));

        assertThat(nestedConditions.get(1).getCondition(), is("num < 100"));

        assertThat(nestedConditions.get(0).isFirst(), is(true));
        assertThat(nestedConditions.get(1).isFirst(), is(false));
        assertThat(nestedConditions.get(2).isFirst(), is(false));
    }

    @Test
    public void omittedIfStatement() throws IOException {
        MethodDef result = getBranchVisitResult(testName.getMethodName());

        List<CvStatement> branchStatements = result.getStatements().stream()
                .filter(BranchStatement.class::isInstance).collect(Collectors.toList());
        assertThat(branchStatements.size(), is(1));

        List<ConditionalStatement> conditionalStatements = ((BranchStatement) branchStatements
                .get(0)).getConditions();
        assertThat(conditionalStatements.size(), is(3));

        ConditionalStatement conditionalStatement = conditionalStatements.get(0);
        assertThat(conditionalStatement.getCondition(), is("num == 0 || isTrue()"));

        MethodCallDef methodCall = (MethodCallDef) conditionalStatement.getChildren().get(0);
        assertThat(methodCall.getName(), is("process"));
    }

    public void testFlatLoop(String method) {
        MethodDef methodDef = new MethodDef();

        loopCompilationUnit.getClassByName("LoopController").ifPresent(clazz -> {

            clazz.getMethodsByName(method).get(0).accept(statementVisitor,
                    VisitContext.of(methodDef));

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

    @Test
    public void tryStatement() throws IOException {
        MethodDef result = getBranchVisitResult(testName.getMethodName());

        List<CvStatement> tryStatements = result.getStatements().stream()
                .filter(TryStatement.class::isInstance).collect(Collectors.toList());
        assertThat(tryStatements.size(), is(1));

        TryStatement tryStatement = (TryStatement) tryStatements.get(0);
        List<CvStatement> tryChildren = tryStatement.getChildren();
        assertThat(tryChildren.size(), is(2));

        assertThat(((MethodCallDef) tryChildren.get(0)).getName(), is("read"));
        assertThat(((MethodCallDef) tryChildren.get(1)).getName(), is("process"));

        List<CatchStatement> catchStatements = tryStatement.getCatchStatements();
        assertThat(catchStatements.size(), is(2));

        assertThat(catchStatements.get(0).getParameter(), is(
                "FileNotFoundException | NullPointerException | ArrayIndexOutOfBoundsException\r\n"
                        + "                | NumberFormatException"));
        assertThat(catchStatements.get(1).getParameter(), is("IOException"));

        assertThat(((MethodCallDef) catchStatements.get(1).getChildren().get(0)).getName(),
                is("process3"));

        FinallyStatement finallyStatement = tryStatement.getFinallyStatement();
        assertThat(finallyStatement.getChildren().size(), is(1));

        assertThat(((MethodCallDef) finallyStatement.getChildren().get(0)).getName(),
                is("process4"));
    }

    @Test
    public void nestedTryStatement() throws IOException {
        MethodDef result = getBranchVisitResult(testName.getMethodName());

        List<CvStatement> tryStatements = result.getStatements().stream()
                .filter(TryStatement.class::isInstance).collect(Collectors.toList());
        assertThat(tryStatements.size(), is(1));

        TryStatement tryStatement = (TryStatement) tryStatements.get(0);
        List<CvStatement> tryChildren = tryStatement.getChildren();
        assertThat(tryChildren.size(), is(3));

        TryStatement nestedTry1 = (TryStatement) tryChildren.get(2);
        assertThat(((MethodCallDef) nestedTry1.getChildren().get(0)).getName(), is("process2"));

        List<CatchStatement> catchStatements = tryStatement.getCatchStatements();
        assertThat(catchStatements.get(0).getChildren().size(), is(1));

        TryStatement nestedTry2 = (TryStatement) catchStatements.get(0).getChildren().get(0);
        assertThat(((MethodCallDef) nestedTry2.getCatchStatements().get(0).getChildren().get(0))
                .getName(), is("process3"));
        assertThat(nestedTry2.getCatchStatements().get(0).getParameter(), is("NumberFormatException"));

        FinallyStatement finallyStatement = tryStatement.getFinallyStatement();
        assertThat(finallyStatement.getChildren().size(), is(1));

        TryStatement nestedTry3 = (TryStatement) finallyStatement.getChildren().get(0);
        assertThat(((MethodCallDef) nestedTry3.getCatchStatements().get(0).getChildren().get(0)).getName(),
                is("process4"));
    }

    public MethodDef getBranchVisitResult(String method) throws IOException {
        MethodDef methodDef = new MethodDef();

        branchCompilationUnit.getClassByName("BranchController").ifPresent(clazz -> {

            clazz.getMethodsByName(method).get(0).accept(statementVisitor,
                    VisitContext.of(methodDef));
        });

        return methodDef;
    }
}
