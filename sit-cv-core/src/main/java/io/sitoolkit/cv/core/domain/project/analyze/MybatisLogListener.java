package io.sitoolkit.cv.core.domain.project.analyze;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MybatisLogListener implements StdoutListener {

  public static String REPOSITORY_METHOD_MARKER = "[RepositoryMethod]";
  private static final Pattern MARKER_PATTERN =
      Pattern.compile("^\\s*" + Pattern.quote(REPOSITORY_METHOD_MARKER) + ".*");

  private static final Pattern MYBATIS_PREPARE_PATTERN = Pattern.compile(".*==>  Preparing:.*");
  private static final Pattern MYBATIS_END_PATTERN = Pattern.compile(".*<==.*");

  @Getter private List<SqlPerMethod> sqlLogs = new ArrayList<>();
  private StringBuilder readingSqlLog = new StringBuilder();
  private String readingRepositoryMethod = "";

  @Override
  public void nextLine(String line) {

    System.out.println(line);

    if (MARKER_PATTERN.matcher(line).matches()) {
      String repositoryMethod = StringUtils.substringAfter(line, REPOSITORY_METHOD_MARKER);
      if (!StringUtils.isEmpty(repositoryMethod)) {
        readingRepositoryMethod = repositoryMethod;
      }
    }

    if (MYBATIS_PREPARE_PATTERN.matcher(line).matches()) {
      readingSqlLog.append(StringUtils.substringAfterLast(line, "==>  Preparing: "));
      readingSqlLog.append("\n");
    }

    if (MYBATIS_END_PATTERN.matcher(line).matches()) {
      SqlPerMethod sqlLog = new SqlPerMethod(readingRepositoryMethod, readingSqlLog.toString());
      log.info("{}", sqlLog);
      sqlLogs.add(sqlLog);

      readingSqlLog = new StringBuilder();
      readingRepositoryMethod = "";
    }
  }
}
