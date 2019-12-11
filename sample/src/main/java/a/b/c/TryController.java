package a.b.c;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

public class TryController {

  @Autowired BProcessor processor;

  @Autowired BService bService;

  public void tryStatement() {
    try (BufferedReader reader = processor.read()) {
      processor.process("");
    } catch (FileNotFoundException
        | NullPointerException
        | ArrayIndexOutOfBoundsException
        | NumberFormatException e) {
      processor.process2("");
    } catch (IOException e) {
      processor.process3("");
    } finally {
      processor.process4("");
    }
  }

  public void tryWithoutFinallyStatement() {
    try {
      processor.process("");
    } catch (NullPointerException | NumberFormatException e) {
      processor.process2("");
    } catch (RuntimeException e) {
      processor.process3("");
    }
  }

  public void nestedTryStatement() {
    try {
      processor.process("");
      try {
        processor.process2("");
      } catch (NullPointerException e) {
        processor.process3("");
      }
    } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
      try {
        processor.process("");
      } catch (NumberFormatException ex) {
        processor.process3("");
      }
    } finally {
      try {
        processor.process3("");
      } catch (RuntimeException ex) {
        processor.process4("");
      }
    }
  }

  public void emptyTryStatement() {
    int i = 0;
    try {
      i++;
    } catch (NullPointerException | NumberFormatException e) {
      i++;
    } catch (RuntimeException e) {
      i++;
    } finally {
      i++;
    }
  }

  public void notEmptyTryStatement() {
    try {
      processor.process("");
    } catch (NullPointerException e) {
    } catch (RuntimeException e) {
    } finally {
    }

    try {
    } catch (NullPointerException e) {
    } catch (RuntimeException e) {
      processor.process2("");
    } finally {
    }

    try {
    } catch (NullPointerException e) {
    } catch (RuntimeException e) {
    } finally {
      processor.process3("");
    }
  }

  public void tryWithResourceStatement() {
    bService.save(new XEntity());
  }
}
