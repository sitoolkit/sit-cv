package sample;

public class SequenceClass2 {

  SequenceClass3 seq3 = new SequenceClass3();
  FieldOfSeqClass field = new FieldOfSeqClass();

  public String sequence1(String str) {
    sequence2();
    return "bar";
  }

  private void sequence2() {
    seq3.sequence3(null, null);
    seq3.sequence4(null, false);
  }

  public void sequence5() {
    return;
  }
}
