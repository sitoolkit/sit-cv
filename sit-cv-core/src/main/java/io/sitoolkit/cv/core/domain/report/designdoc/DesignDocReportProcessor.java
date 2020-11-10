package io.sitoolkit.cv.core.domain.report.designdoc;

import io.sitoolkit.cv.core.domain.menu.MenuItem;
import io.sitoolkit.cv.core.domain.report.Report;
import java.util.List;

public class DesignDocReportProcessor {

  public Report<?> process(List<MenuItem> menuList) {
    return Report.builder().path("assets/designdoc-list.js").content(menuList).build();
  }
}
