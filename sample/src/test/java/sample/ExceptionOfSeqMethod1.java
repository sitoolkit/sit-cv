package sample;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import org.springframework.util.StringUtils;

public class ExceptionOfSeqMethod1 {

  public void throwExceptions(String className)
      throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException,
      InvocationTargetException, InstantiationException {

    if (StringUtils.isEmpty(className)) {
      throw className == null ? new NullPointerException() : new RuntimeException("EMPTY");
    }

    Object obj = Class.forName(className).getConstructor().newInstance();

    if (obj instanceof List && (((List) obj).size() == 0)) {
      throw new ArrayIndexOutOfBoundsException();
    }

    try {
      Thread.sleep(1);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
