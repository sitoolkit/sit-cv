package io.sitoolkit.cv.core.domain.classdef.javaparser;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;

import io.sitoolkit.cv.core.domain.classdef.BranchStatement;
import io.sitoolkit.cv.core.domain.classdef.ConditionalStatement;
import io.sitoolkit.cv.core.domain.classdef.CvStatement;
import io.sitoolkit.cv.core.domain.classdef.MethodCallDef;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;

public class StatementVisitorBranchTest extends StatementVisitorTest {

    static CompilationUnit compilationUnit;

    @BeforeClass
    public static void init() throws IOException {
        compilationUnit = parseFile("src/main/java/a/b/c/BranchController.java");
    }

    @Test
    public void ifStatement() throws IOException {
        MethodDef result = getVisitResult(testName.getMethodName());

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
        MethodDef result = getVisitResult(testName.getMethodName());

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
        MethodDef result = getVisitResult(testName.getMethodName());

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

    public MethodDef getVisitResult(String method) throws IOException {
        return getVisitResult(compilationUnit, "BranchController", method);
    }

}
