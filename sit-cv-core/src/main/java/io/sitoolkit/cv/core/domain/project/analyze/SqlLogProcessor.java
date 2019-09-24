package io.sitoolkit.cv.core.domain.project.analyze;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.config.FilterCondition;
import io.sitoolkit.cv.core.infra.config.FilterConditionGroup;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;

public class SqlLogProcessor {

    public void process(String projectType, SitCvConfig config, Path agentJar, Project project,
            Function<String, ProcessCommand> commandBuilder) {

        SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

        String javaAgentParameter = buildAgentParameter(agentJar, projectType,
                config.getSourceUrl(), config.getRepositoryFilter());
        SqlLogListener sqlLogListener = new SqlLogListener(config.getSqlEnclosureFilter());

        ProcessCommand command = commandBuilder.apply(javaAgentParameter);
        command.stdout(sqlLogListener).execute();

        JsonUtils.obj2file(sqlLogListener.getSqlLogs(), project.getSqlLogPath());
    }

    private String buildAgentParameter(Path agentJar, String projectType, URL configUrl, FilterConditionGroup repositoryFilter) {
        Map<String, String> agentArgsMap = new HashMap<>();
		putRepositoryFilter(agentArgsMap, repositoryFilter);
		agentArgsMap.put("projectType", projectType);
		agentArgsMap.put("repositoryMethodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));
        return "-javaagent:" + agentJar.toString() + agentArgs;
    }
    
    private void putRepositoryFilter(Map<String, String> agentArgsMap, FilterConditionGroup repositoryFilter){
		List<FilterCondition> include = repositoryFilter.getInclude();
		int index = 0;
		for (FilterCondition filterCondition : include) {
			index++;
			String annotation = filterCondition.getAnnotation();
			String name = filterCondition.getName();
			agentArgsMap.put("repositoryFilter" + index + ".annotation", StringUtils.defaultString(annotation));
			agentArgsMap.put("repositoryFilter" + index + ".name", StringUtils.defaultString(name));
		}
    }
}
