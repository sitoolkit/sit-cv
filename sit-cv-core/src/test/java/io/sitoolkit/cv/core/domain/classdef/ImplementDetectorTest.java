package io.sitoolkit.cv.core.domain.classdef;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;

public class ImplementDetectorTest {

    static ImplementDetector detector;

    @BeforeClass
    public static void init() {
        detector = new ImplementDetector();
    }

    @Test
    public void detectImplMethod() {
        ClassDef clazz1 = createInterfaceClassDef("a.b.c", "Service");
        MethodDef method1 = createMethodDef(clazz1, "service1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createClassDef("a.b.c", "ServiceImpl");
        MethodDef method2 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2);
        clazz1.getKnownImplClasses().add(clazz2);

        MethodCallDef methodCallDef = createMethodCallDef(method1);

        MethodDef implMethod = detector.detectImplMethod(methodCallDef);
        assertThat(implMethod, equalTo(method2));
    }

    @Test
    public void detectMultiImpl() {
        ClassDef clazz1 = createInterfaceClassDef("a.b.c", "Service");
        MethodDef method1 = createMethodDef(clazz1, "service1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createClassDef("a.b.c", "ServiceImpl1");
        MethodDef method2 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2);
        clazz1.getKnownImplClasses().add(clazz2);

        ClassDef clazz3 = createClassDef("a.b.c", "ServiceImpl2");
        MethodDef method3 = createMethodDef(clazz3, "service1()");
        clazz3.getMethods().add(method3);
        clazz1.getKnownImplClasses().add(clazz3);

        MethodCallDef methodCallDef = createMethodCallDef(method1);

        MethodDef implMethod = detector.detectImplMethod(methodCallDef);
        assertThat(implMethod, equalTo(method1));
    }

    @Test
    public void detectUnresolveClassDef() {
        ClassDef clazz1 = createInterfaceClassDef("a.b.c", "Service");
        MethodDef method1 = createMethodDef(clazz1, "service1()");
        clazz1.getMethods().add(method1);

        ClassDef clazz2 = createClassDef("a.b.c", "ServiceImpl1");
        MethodDef method2 = createMethodDef(clazz2, "service1()");
        clazz2.getMethods().add(method2);
        clazz1.getKnownImplClasses().add(clazz2);

        MethodCallDef methodCallDef = createMethodCallDef(method1);
        methodCallDef.setClassDef(null);

        MethodDef implMethod = detector.detectImplMethod(methodCallDef);
        assertThat(implMethod, equalTo(methodCallDef));
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
        return methodCallDef;
    }

}
