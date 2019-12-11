package sample;

public class SequenceClass1 {

  public int publicField;
  protected int protectedField;
  int defaultAccessField;
  private int privateFieldPrimitive;

  SequenceClass2 seq2 = new SequenceClass2();

  public void entryPoint() {
    seq2.sequence1("foo");
    seq2.sequence5();
  }

  private void privateMethod() {};

  protected void protectedMethod() {};

  void defaultAccessMethod() {};
}
