# Code Visualizer Core


## How to export report in your application

Add sit-cv-core dependency to your pom.xml

```xml
    <dependency>
      <groupId>io.sitoolkit.cv</groupId>
      <artifactId>sit-cv-core</artifactId>
      <version>1.0.0-beta.1-SNAPSHOT</version>
    </dependency>
```

Sample java code is this.

```java
import io.sitoolkit.cv.core.app.report.ReportService;
import io.sitoolkit.cv.core.app.config.ServiceFactory;

public class Main {

  public static void main(String[] args) {
    ServiceFactory factory = ServiceFactory.initialize(Paths.get("/path/to/your-project"));
    ReportService reportService = factory.getReportService();
    reportService.export();
  }

}
```

After Main is executed successfully, report files will be exported to /path/to/your-project/docs/desingdocs directory and top page is index.html in it.

