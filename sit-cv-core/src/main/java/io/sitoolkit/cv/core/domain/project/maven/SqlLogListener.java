package io.sitoolkit.cv.core.domain.project.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.infra.config.SqlEnclosureFilter;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLogListener implements StdoutListener {

    public static String REPOSITORY_METHOD_MARKER = "[RepositoryMethod]";

    private static Pattern MARKER_PATTERN = Pattern
            .compile("^" + Pattern.quote(REPOSITORY_METHOD_MARKER) + ".*");

    @Getter
    private List<SqlPerMethod> sqlLogs = new ArrayList<>();
    private StringBuilder readingSqlLog = new StringBuilder();
    private String readingRepositoryMethod = "";
    private boolean sqlLogging = false;
    private SqlEnclosureFilter sqlEnclosureFilter;

    public SqlLogListener(SqlEnclosureFilter sqlEnclosureFilter) {
        this.sqlEnclosureFilter = sqlEnclosureFilter;
    }

    @Override
    public void nextLine(String line) {

        System.out.println(line);

        if (sqlLogging) {

            if (sqlEnclosureFilter.getEnd().match(line) || MARKER_PATTERN.matcher(line).matches()) {

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

        if (line.startsWith(REPOSITORY_METHOD_MARKER)) {
            String repositoryMethod = StringUtils.substringAfter(line, REPOSITORY_METHOD_MARKER);
            if (!StringUtils.isEmpty(repositoryMethod)) {
                readingRepositoryMethod = repositoryMethod;
            }
        }

        if (!StringUtils.isEmpty(readingRepositoryMethod)
                && sqlEnclosureFilter.getStart().match(line)) {
            sqlLogging = true;
        }
    }

}