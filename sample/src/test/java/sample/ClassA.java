package sample;

import java.util.List;

public class ClassA {

    public int publicField;
    protected int protectedField;
    int defaultAccessField;
    private int privateFieldPrimitive;
    
    private List<String> list;
    private ClassB classB;
    
    public void publicMethod() {
        classB.method();
    };
    private void privateMethod() {};
    protected void protectedMethod() {};
    void defaultAccessMethod() {};

}
