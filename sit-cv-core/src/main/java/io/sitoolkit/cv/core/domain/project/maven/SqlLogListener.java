package io.sitoolkit.cv.core.domain.project.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLogListener implements StdoutListener {

    @Getter
    private List<SqlLog> sqlLogs = new ArrayList<>();
    private StringBuilder readingSqlLog = new StringBuilder();
    private String readingRepositoryMethod = "";
    private boolean sqlLogging = false;
    private Pattern sqlLogStartPattern = Pattern.compile(".*org.hibernate.SQL.*");
    private Pattern sqlLogEndPattern = Pattern.compile("^[0-9]{4}-.*");

    @Override
    public void nextLine(String line) {

        System.out.println(line);

        if (sqlLogging) {

            if (sqlLogEndPattern.matcher(line).matches()) {

                if (StringUtils.isNotEmpty(readingRepositoryMethod)) {
                    SqlLog sqlLog = new SqlLog(readingRepositoryMethod, readingSqlLog.toString());

                    log.info("{}", sqlLog);

                    sqlLogs.add(sqlLog);

                }

                sqlLogging = false;
                readingSqlLog = new StringBuilder();
                readingRepositoryMethod = "";

                return;
            }

            readingSqlLog.append(line);
            readingSqlLog.append("\n");
        }

        String repositoryMethod = StringUtils.substringAfter(line, "[RepositoryMethod]");
        if (!StringUtils.isEmpty(repositoryMethod)) {
            readingRepositoryMethod = repositoryMethod;
        }

        if (sqlLogStartPattern.matcher(line).matches()) {
            sqlLogging = true;
        }
    }

    @Data
    @AllArgsConstructor
    public static class SqlLog {
        private String repositoryMethod;
        private String sqlText;
    }
}