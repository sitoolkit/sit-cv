package io.sitoolkit.cv.tools.infra.config;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.sitoolkit.cv.tools.infra.util.JsonUtils;

public class RepositoryLoggerArgumentParser {

    private static final String ITEM_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public RepositoryLoggerConfig parse(String args) {
        Map<String, String> valueMap = parseArgs(args);

//        RepositoryLoggerConfig config = readConfig(valueMap.get("configUrl"));
        RepositoryLoggerConfig config = new RepositoryLoggerConfig();
        config.setRepositoryFilter(getRepositoryFilter(valueMap));
        config.setRepositoryMethodMarker(valueMap.get("repositoryMethodMarker"));
        config.setProjectType(valueMap.get("projectType"));

        return config;
    }

    private FilterConditionGroup getRepositoryFilter(Map<String, String> valueMap) {
        FilterConditionGroup fcg = new FilterConditionGroup();
        List<FilterCondition> include = new ArrayList<>();
        fcg.setInclude(include);

        int index = 0;
        Optional<FilterCondition> gotFilter;
        do {
            index++;
            gotFilter = getSingleRepositoryFilter(valueMap, index);
            gotFilter.ifPresent(include::add);

        } while (gotFilter.isPresent());

        return fcg;
    }

    private Optional<FilterCondition> getSingleRepositoryFilter(Map<String, String> valueMap, int index) {
        String annotation = valueMap.get("repositoryFilter" + index + ".annotation");
        String name = valueMap.get("repositoryFilter" + index + ".name");
        if (annotation == null || name == null) {
            return Optional.empty();
        } else {
            return Optional.of(new FilterCondition(name, annotation));
        }
    }

	private Map<String, String> parseArgs(String args) {
        Map<String, String> valueMap = new HashMap<>();
        for (String item : args.split(ITEM_SEPARATOR)) {
            String[] keyValue = item.split(KEY_VALUE_SEPARATOR);

            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            valueMap.put(key, value);
        }
        
        return valueMap;
    }
    
    private RepositoryLoggerConfig readConfig(String configUrl) {
        try {
            URL url = new URL(configUrl);
            return JsonUtils.url2obj(url, RepositoryLoggerConfig.class);
        } catch (MalformedURLException e) {
            throw new UncheckedIOException(e);
        }
    }

}
