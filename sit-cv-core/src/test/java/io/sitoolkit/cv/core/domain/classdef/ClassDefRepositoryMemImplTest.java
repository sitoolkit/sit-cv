package io.sitoolkit.cv.core.domain.classdef;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

import io.sitoolkit.cv.core.infra.config.SitCvConfig;

public class ClassDefRepositoryMemImplTest {

    static ClassDefRepository repository;

    @BeforeClass
    public static void init() {
        repository = new ClassDefRepositoryMemImpl(new SitCvConfig());
    }

    @Test
    public void resolveMethodCallsAndStatements() {
        ClassDef clazz1 = createClassDef("a.b.c", "Controller");
        MethodDef method1 = createMethodDef(clazz1, "controller1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createClassDef("a.b.c", "Service");
        MethodDef method2 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2);

        ClassDef clazz3 = createClassDef("a.b.c", "Repository");
        MethodDef method3_1 = createMethodDef(clazz3, "repository1()");
        clazz3.getMethods().add(method3_1);
        MethodDef method3_2 = createMethodDef(clazz3, "repository2()");
        clazz3.getMethods().add(method3_2);

        BranchStatement branch = new BranchStatement();
        ConditionalStatement condition = new ConditionalStatement();
        condition.setCondition("flag == true");
        MethodCallDef methodCall1 = createMethodCallDef(method2);
        condition.getChildren().add(methodCall1);
        branch.getConditions().add(condition);

        LoopStatement loop = new LoopStatement();
        loop.setScope("i < 10");
        MethodCallDef methodCall2 = createMethodCallDef(method3_1);
        loop.getChildren().add(methodCall2);

        MethodCallDef methodCall3 = createMethodCallDef(method3_2);

        method1.getMethodCalls().add(methodCall1);
        method1.getStatements().add(branch);
        method2.getMethodCalls().add(methodCall2);
        method2.getStatements().add(loop);
        method3_1.getMethodCalls().add(methodCall3);
        method3_1.getStatements().add(methodCall3);

        repository.save(clazz1);
        repository.save(clazz2);
        repository.save(clazz3);

        repository.solveReferences();

        BranchStatement statementResult1 = (BranchStatement) repository
                .findMethodByQualifiedSignature("a.b.c.Controller.controller1()").getStatements()
                .get(0);
        MethodCallDef methodCallResult1 = (MethodCallDef) statementResult1.getConditions().get(0)
                .getChildren().get(0);
        assertThat(methodCallResult1.getQualifiedSignature(), is("a.b.c.Service.service1()"));
        assertThat(methodCallResult1.getMethodCalls().size(), is(1));
        assertThat(methodCallResult1.getStatements().size(), is(1));

        LoopStatement statementResult2 = (LoopStatement) methodCallResult1.getStatements().get(0);
        assertThat(statementResult2.getScope(), is("i < 10"));

        MethodCallDef methodCallResult2 = (MethodCallDef) statementResult2.getChildren().get(0);
        assertThat(methodCallResult2.getQualifiedSignature(), is("a.b.c.Repository.repository1()"));
        assertThat(methodCallResult2.getMethodCalls().size(), is(1));
        assertThat(methodCallResult2.getStatements().size(), is(1));
        assertThat(methodCallResult1.getMethodCalls().iterator().next() == methodCallResult2,
                is(true));

        MethodCallDef methodCallResult3 = (MethodCallDef) methodCallResult2.getStatements().get(0);
        assertThat(methodCallResult3.getQualifiedSignature(), is("a.b.c.Repository.repository2()"));
        assertThat(methodCallResult2.getMethodCalls().iterator().next() == methodCallResult3,
                is(true));
    }

    @Test
    public void resolveInfiniteMethodCalls() {
        ClassDef clazz1 = createClassDef("a.b.c", "Controller");
        MethodDef method1 = createMethodDef(clazz1, "controller1()");
        clazz1.getMethods().add(method1);
        MethodDef method2 = createMethodDef(clazz1, "controller2()");
        clazz1.getMethods().add(method2);

        method1.getMethodCalls().add(createMethodCallDef(method2));
        method2.getMethodCalls().add(createMethodCallDef(method1));

        repository.save(clazz1);

        repository.solveReferences();

        MethodDef methodResult1 = repository
                .findMethodByQualifiedSignature("a.b.c.Controller.controller1()");
        assertThat(methodResult1.getMethodCalls().size(), is(1));

        MethodCallDef methodCallResult1 = methodResult1.getMethodCalls().iterator().next();
        assertThat(methodCallResult1.getMethodCalls().size(), is(1));
        assertThat(methodCallResult1.getMethodCalls().iterator().next().getQualifiedSignature(),
                is("a.b.c.Controller.controller1()"));
        assertThat(methodCallResult1.getMethodCalls().iterator().next().getMethodCalls().iterator()
                .next().getQualifiedSignature(), is("a.b.c.Controller.controller2()"));
    }

    @Test
    public void resolveTypeRef() {
        ClassDef clazz1 = createClassDef("a.b.c", "Controller");
        MethodDef method1 = createMethodDef(clazz1, "controller1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createClassDef("a.b.c", "Service");
        MethodDef method2_1 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2_1);

        ClassDef entity1 = createClassDef("a.b.c", "Entity1");
        ClassDef entity2 = createClassDef("a.b.c", "Entity2");

        MethodCallDef methodCall1 = createMethodCallDef(method2_1);
        methodCall1.getParamTypes().add(createTypeDef("a.b.c.Entity1"));
        methodCall1.setReturnType(createTypeDef("a.b.c.Entity2"));
        method1.getMethodCalls().add(methodCall1);

        repository.save(clazz1);
        repository.save(clazz2);
        repository.save(entity1);
        repository.save(entity2);

        repository.solveReferences();

        MethodDef methodResult1 = repository
                .findMethodByQualifiedSignature("a.b.c.Controller.controller1()");
        MethodCallDef methodCallResult1 = methodResult1.getMethodCalls().iterator().next();
        assertThat(methodCallResult1.getParamTypes().get(0).getClassRef(), is(entity1));
        assertThat(methodCallResult1.getReturnType().getClassRef(), is(entity2));
    }

    private ClassDef createClassDef(String pkg, String name) {
        ClassDef classDef = new ClassDef();
        classDef.setPkg(pkg);
        classDef.setName(name);
        return classDef;
    }

    private MethodDef createMethodDef(ClassDef classDef, String name) {
        MethodDef methodDef = new MethodDef();
        methodDef.setQualifiedSignature(classDef.getFullyQualifiedName() + "." + name);
        methodDef.setName(name);
        methodDef.setClassDef(classDef);
        methodDef.setParamTypes(new ArrayList<>());
        methodDef.setReturnType(createTypeDef("void"));
        return methodDef;
    }

    private MethodCallDef createMethodCallDef(MethodDef methodDef) {
        MethodCallDef methodCallDef = new MethodCallDef();
        methodCallDef.setQualifiedSignature(methodDef.getQualifiedSignature());
        methodCallDef.setParamTypes(new ArrayList<>());
        methodCallDef.setReturnType(createTypeDef("void"));
        return methodCallDef;
    }

    private TypeDef createTypeDef(String name) {
        TypeDef typeDef = new TypeDef();
        typeDef.setName(name);
        return typeDef;
    }
}
