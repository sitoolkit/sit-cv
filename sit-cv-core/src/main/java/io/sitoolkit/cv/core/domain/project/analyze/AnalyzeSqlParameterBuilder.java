package io.sitoolkit.cv.core.domain.project.analyze;

import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyzeSqlParameterBuilder {

    public static String build(Path agentJarPath, String projectType, URL configUrl) {
        Map<String, String> agentArgsMap = new HashMap<>();
        agentArgsMap.put("projectType", projectType);
        agentArgsMap.put("configUrl", configUrl.toString());
        agentArgsMap.put("repositoryMethodMarker", SqlLogListener.REPOSITORY_METHOD_MARKER);
        String agentArgs = agentArgsMap.entrySet().stream()
                .map((e) -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining(";", "=", ""));
        return "-javaagent:" + agentJarPath.toString() + agentArgs;
    }
    
}
