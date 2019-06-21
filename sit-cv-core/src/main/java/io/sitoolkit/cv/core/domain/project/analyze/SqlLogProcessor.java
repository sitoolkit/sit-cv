package io.sitoolkit.cv.core.domain.project.analyze;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.sitoolkit.cv.core.domain.project.Project;
import io.sitoolkit.cv.core.infra.config.SitCvConfig;
import io.sitoolkit.cv.core.infra.util.JsonUtils;
import io.sitoolkit.cv.core.infra.util.SitFileUtils;
import io.sitoolkit.util.buildtoolhelper.process.ProcessCommand;

public class SqlLogProcessor {

    public void process(String projectType, SitCvConfig config, Path agentJar, Project project,
            Function<String, ProcessCommand> commandBuilder) {

        SitFileUtils.createDirectories(project.getSqlLogPath().getParent());

        String javaAgentParameter = buildAgentParameter(agentJar, projectType,
                config.getSourceUrl());
        SqlLogListener sqlLogListener = new SqlLogListener(config.getSqlEnclosureFilter());

        ProcessCommand command = commandBuilder.apply(javaAgentParameter);
        command.stdout(sqlLogListener).execute();

        JsonUtils.obj2file(sqlLogListener.getSqlLogs(), project.getSqlLogPath());
    }

    private String buildAgentParameter(Path agentJar, String projectType, URL configUrl) {
        Map<String, String> agentArgsMap = new HashMap<>();
        agentArgsMap.put("projectType", projectType);
        agentArgsMap.put("configUrl", configUrl.toString());
        agentArgsMap.put("repositoryMethodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));
        return "-javaagent:" + agentJar.toString() + agentArgs;
    }

}
