package io.sitoolkit.cv.app.infra.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;

public class BrowserUtils {

  public static void open(String uri) {
    try {
      Desktop.getDesktop().browse(new URI(uri));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
