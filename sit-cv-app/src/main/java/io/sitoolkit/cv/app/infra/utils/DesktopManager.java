package io.sitoolkit.cv.app.infra.utils;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DesktopManager {

  @Value("${server.port:8080}")
  private int port;

  public void openBrowser() {
    try {
      URI uri = new URI("http://localhost:" + port);
      Desktop.getDesktop().browse(uri);
    } catch (URISyntaxException | IOException e) {
      log.warn("Exception opening browser", e);
    }
  }
}
