package a.b.c;


import org.springframework.beans.factory.annotation.Autowired;

public class BranchController {

  @Autowired BProcessor processor;

  public void ifStatement(int num, String str) {
    /** if comment */
    if ((num == 0 || (isTrue()))) {
      processor.process("");
    } else if (num < 10
        || isTrue() // if comment
        || false) {
      processor.process2("");
    } else {
      processor.process3("");
    }
  }

  public void omittedIfStatement(int num, String str) {
    if (num == 0 || isTrue()) processor.process("");
    else if (num < 10 || isTrue() || false) processor.process2("");
    else processor.process3("");
  }

  public void nestedIfStatement(int num, String str) {
    if (num == 0 || isTrue()) {
      processor.process("");
    } else if (num < 10 || false) {
      processor.process2("");
    } else {
      if (num > 1000) {
        processor.process2("");
      } else if (num < 100) {
        // nothing
      } else {
        processor.process("");
      }

      processor.process3("");
    }
  }

  public void blankIfStatement(int num, String str) {
    int i;
    if (num == 0 || isTrue()) {
      i = 0;
    } else if (num < 10 || isTrue()) {
      i = 1;
    } else {
      i = 2;
    }

    processor.process("");
  }

  public void switchStatement(String str) {
    switch (str) {
      case "ABC":
        processor.process("");
        break;
      case "DEF":
      case "XYZ":
        processor.process2("");
        break;
      default:
        processor.process3("");
        break;
    }
  }

  public void ternaryOperatorStatement(int num) {
    String result =
        num == 2
            ? processor.process("")
            : num < 20 ? processor.process2("") : processor.process3("");
  }

  private boolean isTrue() {
    return true;
  }
}
