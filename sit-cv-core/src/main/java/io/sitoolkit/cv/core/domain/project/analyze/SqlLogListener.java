package io.sitoolkit.cv.core.domain.project.analyze;

import io.sitoolkit.cv.core.infra.config.CvConfig;
import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.LifelineClasses;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import java.util.stream.Collectors;
import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.infra.config.EnclosureFilterCondition;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLogListener implements StdoutListener {

    public static String REPOSITORY_METHOD_MARKER = "[RepositoryMethod]";

    private static Pattern MARKER_PATTERN = Pattern
            .compile("^\\s*" + Pattern.quote(REPOSITORY_METHOD_MARKER) + ".*");

    @Getter
    private List<SqlPerMethod> sqlLogs = new ArrayList<>();
    private StringBuilder readingSqlLog = new StringBuilder();
    private String readingRepositoryMethod = "";
    private boolean sqlLogging = false;
    private EnclosureFilterCondition sqlEnclosureFilter;
    private List<FilterCondition> filterConditions;

    public SqlLogListener(CvConfig config) {
      this.sqlEnclosureFilter = config.getSqlEnclosureFilter();
      this.filterConditions = config.getLifelines().stream()
          .filter(LifelineClasses::isExclude)
          .map(LifelineClasses::getCondition)
          .collect(Collectors.toList());
    }

    @Override
    public void nextLine(String line) {

        System.out.println(line);

        boolean isMarkerLine = MARKER_PATTERN.matcher(line).matches();
        
        if (sqlLogging) {

            if (isMarkerLine || sqlEnclosureFilter.matchEnd(line)) {

                if (StringUtils.isNotEmpty(readingRepositoryMethod)) {
                    SqlPerMethod sqlLog = new SqlPerMethod(readingRepositoryMethod,
                            readingSqlLog.toString());

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
            if (!StringUtils.isEmpty(repositoryMethod) && !matchExclude(repositoryMethod)) {
                readingRepositoryMethod = repositoryMethod;
            }
        }

        if (!StringUtils.isEmpty(readingRepositoryMethod) && sqlEnclosureFilter.matchStart(line)) {
            sqlLogging = true;
        }
    }

    private boolean matchExclude(String method) {
      if (filterConditions.isEmpty()) return false;

      String beforeMethodArgs = StringUtils.substringBefore(method, "(");

      Deque<String> fullNameDeq = new LinkedList<>(Arrays.asList(beforeMethodArgs.split("\\.")));
      Iterator<String> names = fullNameDeq.descendingIterator();

      boolean exclude = false;
      while(names.hasNext() && !exclude) {
        String name = names.next();
        exclude = filterConditions.stream()
            .filter(x -> x.matchName(name)).findFirst().isPresent();
      }

      return exclude;
    }

}
