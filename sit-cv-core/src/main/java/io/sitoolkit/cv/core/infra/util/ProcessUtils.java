package io.sitoolkit.cv.core.infra.util;

import io.sitoolkit.cv.core.infra.exception.ProcessExecutionException;

public class ProcessUtils {

  public static void start(String... command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    try {
      Process process = processBuilder.start();
      int ret = process.waitFor();
      if (ret != 0) {
        throw new Exception(String.format("Processing return code: %d", ret));
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      throw new ProcessExecutionException(e);
    }
  }

}
