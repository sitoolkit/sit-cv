package io.sitoolkit.cv.app;

import java.awt.Desktop;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.DefaultApplicationArguments;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import io.sitoolkit.cv.app.infra.config.SitCvApplicationOption;
import io.sitoolkit.cv.core.app.config.ServiceFactory;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class SitCvApplication {

  @Autowired
  private ApplicationContext appCtx;

  public static void main(String[] args) {
    ApplicationArguments appArgs = new DefaultApplicationArguments(args);

    if (appArgs.containsOption(SitCvApplicationOption.ANALYZE_SQL.getKey())) {
      executeAnalyzeSqlMode(appArgs);
    }

    if (appArgs.containsOption(SitCvApplicationOption.REPORT.getKey())) {
      executeReportMode(appArgs);
    } else {
      executeServerMode(args, appArgs);
    }
  }

  private static Path getProjectDir(ApplicationArguments appArgs) {
    List<String> values = appArgs.getOptionValues(SitCvApplicationOption.PROJECT.getKey());
    String projectPath = SitCvApplicationOption.getOptionValue(values, ".");
    return Paths.get(projectPath);
  }

  private static void executeReportMode(ApplicationArguments appArgs) {
    ServiceFactory.createAndInitialize(getProjectDir(appArgs), false).getReportService().export();
  }

  private static void executeAnalyzeSqlMode(ApplicationArguments appArgs) {
    ServiceFactory.create(getProjectDir(appArgs), false).getCrudService().analyzeSql();
  }

  private static void executeServerMode(String[] args, ApplicationArguments appArgs) {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(SitCvApplication.class);
    ApplicationContext appCtx = builder.headless(false).run(args);

    if (needsOpenBrowser(appArgs)) {
      openBrowser(appCtx);
    }
  }

  private static boolean needsOpenBrowser(ApplicationArguments appArgs) {
    List<String> values = appArgs.getOptionValues(SitCvApplicationOption.OPEN_BROWSER.getKey());
    String openValue = SitCvApplicationOption.getOptionValue(values, "true");
    return !openValue.equals("false");
  }

  private static void openBrowser(ApplicationContext appCtx) {
    Environment env = appCtx.getBean(Environment.class);
    String port = env.getProperty("local.server.port");

    try {
      URI uri = new URI("http://localhost:" + port);
      Desktop.getDesktop().browse(uri);
    } catch (Exception e) {
      log.error("Exception opening browser", e);
    }
  }

  /**
   * Initialize the service before the screen is reloaded by LiveReload of
   * spring-boot-devtools. The screen reload process is triggered when
   * ContextRefreshedEvent is notified.
   *
   * @see org.springframework.boot.devtools.autoconfigure.LocalDevToolsAutoConfiguration
   */
  @PostConstruct
  public void initialize() {
    appCtx.getBean(ServiceFactory.class).initialize();
  }

}
