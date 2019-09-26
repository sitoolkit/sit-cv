package io.sitoolkit.cv.core.infra.util;

import io.sitoolkit.cv.core.infra.exception.ProcessExecutionException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessUtils {

  public static void start(String... command) {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    log.info("Starting process with command {}",
            processBuilder.command().stream()
                    .reduce((s1, s2) -> String.join(" ", s1, s2)).orElse(""));
    try {
      Process process = processBuilder.start();
      int retCode = process.waitFor();
      if (retCode != 0) {
        throw new ProcessExecutionException(retCode);
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    } catch (Exception e) {
      throw new ProcessExecutionException(e);
    }
  }

}
