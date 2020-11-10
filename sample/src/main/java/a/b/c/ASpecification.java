package a.b.c;

import java.util.List;

public class ASpecification {

  public boolean isSatisfiedByList(List<XEntity> targetList) {
    for (XEntity target : targetList) {
      boolean result = isSatisfiedBy(target);
      if (!result) {
        return false;
      }
    }
    return true;
  }

  public boolean isSatisfiedBy(XEntity target) {
    return true;
  }
}
