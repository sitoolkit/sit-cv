package io.sitoolkit.cv.app;

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
import io.sitoolkit.cv.app.infra.config.SitCvApplicationOption;
import io.sitoolkit.cv.app.infra.utils.BrowserUtils;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

@SpringBootApplication
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
    List<String> projects = appArgs.getOptionValues(SitCvApplicationOption.PROJECT.getKey());
    return projects == null || projects.isEmpty() ? Paths.get(".") : Paths.get(projects.get(0));
  }

  private static void executeReportMode(ApplicationArguments appArgs) {
    ServiceFactory.createAndInitialize(getProjectDir(appArgs), false).getReportService().export();
  }

  private static void executeAnalyzeSqlMode(ApplicationArguments appArgs) {
    ServiceFactory.create(getProjectDir(appArgs), false).getCrudService().analyzeSql();
  }

  private static void executeServerMode(String[] args, ApplicationArguments appArgs) {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(SitCvApplication.class);
    builder.headless(false).run(args);

    if (hasOpenBrowserOption(appArgs)) {
      BrowserUtils.open("http://localhost:8080");
    }
  }

  private static boolean hasOpenBrowserOption(ApplicationArguments appArgs) {
    List<String> openValues = appArgs.getOptionValues(SitCvApplicationOption.OPEN_BROWSER.getKey());
    return openValues == null || openValues.size() <= 0 || openValues.get(0).equals("true");
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
