package io.sitoolkit.cv.core.domain.project.analyze;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.config.CvConfig;
import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;

public class SqlLogProcessor {

  public void process(
      String projectType,
      CvConfig config,
      Path agentJar,
      Project project,
      Function<String, ProcessCommand> commandBuilder) {

    SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

    String javaAgentParameter =
        buildAgentParameter(agentJar, projectType, config.getRepositoryFilter());
    SqlLogListener sqlLogListener = new SqlLogListener(config.getSqlEnclosureFilter());

    ProcessCommand command = commandBuilder.apply(javaAgentParameter);
    command.stdout(sqlLogListener).execute();

    JsonUtils.obj2file(sqlLogListener.getSqlLogs(), project.getSqlLogPath());
  }

  private String buildAgentParameter(
      Path agentJar, String projectType, FilterConditionGroup repositoryFilter) {
    Map<String, String> agentArgsMap = new HashMap<>();
    putRepositoryFilter(agentArgsMap, repositoryFilter);
    agentArgsMap.put("projectType", projectType);
    agentArgsMap.put("repositoryMethodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
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
