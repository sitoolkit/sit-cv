package io.sitoolkit.cv.core.domain.classdef;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.BeforeClass;
import org.junit.Test;

public class ImplementCollectorTest {

    static ImplementCollector collector;

    @BeforeClass
    public static void init() {
        collector = new ImplementCollector(new ImplementDetector());
    }

    @Test
    public void collectMethodCallsRecursively() {
        ClassDef clazz1 = createClassDef("a.b.c", "Controller");
        MethodDef method1 = createMethodDef(clazz1, "controller1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createInterfaceClassDef("a.b.c", "Service");
        MethodDef method2 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2);

        ClassDef clazz3 = createClassDef("a.b.c", "ServiceImpl");
        MethodDef method3 = createMethodDef(clazz3, "service1()");
        clazz3.getMethods().add(method3);
        clazz2.getKnownImplClasses().add(clazz3);

        ClassDef clazz4 = createClassDef("a.b.c", "Repository");
        MethodDef method4 = createMethodDef(clazz4, "repository1()");
        clazz4.getMethods().add(method4);

        method1.getMethodCalls().add(createMethodCallDef(method2));
        method3.getMethodCalls().add(createMethodCallDef(method4));
        method3.getMethodCalls().add(createMethodCallDef(method4));

        List<MethodDef> implMethodCalls = collector.collectMethodCallsRecursively(method1)
                .collect(Collectors.toList());
        assertThat(implMethodCalls.size(), is(3));
        assertThat(implMethodCalls.get(0).getQualifiedSignature(), is("a.b.c.Controller.controller1()"));
        assertThat(implMethodCalls.get(1).getQualifiedSignature(), is("a.b.c.ServiceImpl.service1()"));
        assertThat(implMethodCalls.get(2).getQualifiedSignature(), is("a.b.c.Repository.repository1()"));

        implMethodCalls = collector.collectMethodCallsRecursively(method2)
                .collect(Collectors.toList());
        assertThat(implMethodCalls.size(), is(1));
        assertThat(implMethodCalls.get(0).getQualifiedSignature(), is("a.b.c.Service.service1()"));
    }

    private ClassDef createClassDef(String pkg, String name) {
        ClassDef classDef = new ClassDef();
        classDef.setPkg(pkg);
        classDef.setName(name);
        return classDef;
    }

    private ClassDef createInterfaceClassDef(String pkg, String name) {
        ClassDef classDef = createClassDef(pkg, name);
        classDef.setType(ClassType.INTERFACE);
        return classDef;
    }

    private MethodDef createMethodDef(ClassDef classDef, String name) {
        MethodDef methodDef = new MethodDef();
        methodDef.setQualifiedSignature(classDef.getFullyQualifiedName() + "." + name);
        methodDef.setName(name);
        methodDef.setClassDef(classDef);
        return methodDef;
    }

    private MethodCallDef createMethodCallDef(MethodDef methodDef) {
        MethodCallDef methodCallDef = new MethodCallDef();
        methodCallDef.setQualifiedSignature(methodDef.getQualifiedSignature());
        methodCallDef.setClassDef(methodDef.getClassDef());
        methodCallDef.setMethodCalls(methodDef.getMethodCalls());
        return methodCallDef;
    }

}
