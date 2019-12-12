package io.sitoolkit.cv.core.domain.project.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.infra.config.EnclosureFilterCondition;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLogListener implements StdoutListener {

  public static String REPOSITORY_METHOD_MARKER = "[RepositoryMethod]";

  private static Pattern MARKER_PATTERN =
      Pattern.compile("^\\s*" + Pattern.quote(REPOSITORY_METHOD_MARKER) + ".*");

  @Getter private List<SqlPerMethod> sqlLogs = new ArrayList<>();
  private StringBuilder readingSqlLog = new StringBuilder();
  private String readingRepositoryMethod = "";
  private boolean sqlLogging = false;
  private EnclosureFilterCondition sqlEnclosureFilter;

  public SqlLogListener(EnclosureFilterCondition sqlEnclosureFilter) {
    this.sqlEnclosureFilter = sqlEnclosureFilter;
  }

  @Override
  public void nextLine(String line) {

    System.out.println(line);

    if (!sqlEnclosureFilter.matchIgnore(line)) {
      boolean isMarkerLine = MARKER_PATTERN.matcher(line).matches();

      if (sqlLogging) {

        if (isMarkerLine || sqlEnclosureFilter.matchEnd(line)) {

          if (StringUtils.isNotEmpty(readingRepositoryMethod)) {
            SqlPerMethod sqlLog =
                new SqlPerMethod(readingRepositoryMethod, readingSqlLog.toString());

            log.info("{}", sqlLog);

            sqlLogs.add(sqlLog);
          }

          sqlLogging = false;
          readingSqlLog = new StringBuilder();
          readingRepositoryMethod = "";

        } else {
          readingSqlLog.append(line);
          readingSqlLog.append("\n");
        }
      }

      if (isMarkerLine) {
        String repositoryMethod = StringUtils.substringAfter(line, REPOSITORY_METHOD_MARKER);
        if (!StringUtils.isEmpty(repositoryMethod)) {
          readingRepositoryMethod = repositoryMethod;
        }
      }

      if (!StringUtils.isEmpty(readingRepositoryMethod) && sqlEnclosureFilter.matchStart(line)) {
        sqlLogging = true;

        if (sqlEnclosureFilter.isSqlStartsWithStartLine()) {
          readingSqlLog.append(sqlEnclosureFilter.substringAfterStart(line));
          readingSqlLog.append("\n");
        }
      }
    }
  }
}
