package io.sitoolkit.cv.app.infra.config;

import java.util.List;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.boot.ApplicationArguments;

public enum SitCvApplicationOption {
  REPORT,
  PROJECT,
  ANALYZE_SQL("analyze-sql"),
  OPEN_BROWSER("open");

  public static final String PREFIX = "cv";

  private String key;

  private SitCvApplicationOption() {
    this.key = name().toLowerCase();
  }

  private SitCvApplicationOption(String key) {
    this.key = key;
  }

  public String getKey() {
    return PREFIX + "." + key;
  }

  public String getValue(ApplicationArguments appArgs, String defaultValue) {
    List<String> args = appArgs.getOptionValues(this.getKey());
    return CollectionUtils.isEmpty(args) ? defaultValue : args.get(0);
  }
}
