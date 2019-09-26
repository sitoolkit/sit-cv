package io.sitoolkit.cv.core.infra.util;

import java.io.IOException;
import java.util.List;

public class ProcessUtils {

  public static int run(List<String> command) throws IOException, InterruptedException {
    ProcessBuilder processBuilder = new ProcessBuilder(command);
    Process process = processBuilder.start();
    return process.waitFor();
  }

}
