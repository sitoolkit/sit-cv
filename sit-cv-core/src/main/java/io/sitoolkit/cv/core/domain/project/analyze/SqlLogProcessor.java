package io.sitoolkit.cv.core.domain.project.analyze;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.crud.SqlPerMethod;
import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.config.CvConfig;
import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;
import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;

public class SqlLogProcessor {

  public void process(
      String projectType,
      CvConfig config,
      Path agentJar,
      Project project,
      Function<String, ProcessCommand> commandBuilder) {

    SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

    StdoutListener listener;
    String repositoryMethodMaker;
    List<SqlPerMethod> sqlLogs;
    if (config.isAnalyzeMybatis()) {
      listener = new MybatisLogListener();
      repositoryMethodMaker = MybatisLogListener.REPOSITORY_METHOD_MARKER;
      sqlLogs = ((MybatisLogListener) listener).getSqlLogs();
    } else {
      listener = new SqlLogListener(config.getSqlEnclosureFilter());
      repositoryMethodMaker = SqlLogListener.REPOSITORY_METHOD_MARKER;
      sqlLogs = ((SqlLogListener) listener).getSqlLogs();
    }

    String javaAgentParameter =
        buildAgentParameter(
            agentJar, projectType, config.getRepositoryFilter(), repositoryMethodMaker);

    ProcessCommand command = commandBuilder.apply(javaAgentParameter);
    command.stdout(listener).execute();

    JsonUtils.obj2file(sqlLogs, project.getSqlLogPath());
  }

  private String buildAgentParameter(
      Path agentJar,
      String projectType,
      FilterConditionGroup repositoryFilter,
      String repositoryMethodMarker) {
    Map<String, String> agentArgsMap = new HashMap<>();
    putRepositoryFilter(agentArgsMap, repositoryFilter);
    agentArgsMap.put("projectType", projectType);
    agentArgsMap.put("repositoryMethodMarker", repositoryMethodMarker);
    String agentArgs =
        agentArgsMap
            .entrySet()
            .stream()
            .map((e) -> e.getKey() + "=" + e.getValue())
            .collect(Collectors.joining(";", "=", ""));
    return "-javaagent:" + agentJar.toString() + agentArgs;
  }

  private void putRepositoryFilter(
      Map<String, String> agentArgsMap, FilterConditionGroup repositoryFilter) {
    putRepositoryFilter("include.", agentArgsMap, repositoryFilter.getInclude());
    putRepositoryFilter("exclude.", agentArgsMap, repositoryFilter.getExclude());
  }

  private void putRepositoryFilter(
      String prefix, Map<String, String> agentArgsMap, List<FilterCondition> conditions) {
    int index = 0;
    for (FilterCondition filterCondition : conditions) {
      index++;
      String annotation = filterCondition.getAnnotation();
      String name = filterCondition.getName();
      agentArgsMap.put(
          prefix + "repositoryFilter" + index + ".annotation",
          StringUtils.defaultString(annotation));
      agentArgsMap.put(
          prefix + "repositoryFilter" + index + ".name", StringUtils.defaultString(name));
    }
  }
}
