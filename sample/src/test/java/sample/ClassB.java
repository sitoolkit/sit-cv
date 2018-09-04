package sample;

import java.util.List;

public class ClassB {

    List<ClassC> classCs;
    ClassD classD;
    public int method() {
        return classCs.get(0).func();
    }
    public int method2() {
        return classD.method();
    }
}
