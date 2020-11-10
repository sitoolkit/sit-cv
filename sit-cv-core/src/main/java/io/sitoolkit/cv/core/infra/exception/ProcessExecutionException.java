package io.sitoolkit.cv.core.infra.exception;

public class ProcessExecutionException extends RuntimeException {

  public ProcessExecutionException(Throwable cause) {
    super(cause);
  }

  public ProcessExecutionException(int retCode) {
    super(String.format("Return code: %d", retCode));
  }
}
