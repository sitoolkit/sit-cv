package io.sitoolkit.cv.tools.infra.config;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import io.sitoolkit.cv.tools.infra.util.JsonUtils;

public class RepositoryLoggerArgumentParser {

    private static final String ITEM_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";

    public RepositoryLoggerConfig parse(String args) {
        Map<String, String> valueMap = parseArgs(args);

        RepositoryLoggerConfig config = readConfig(valueMap.get("configUrl"));
        config.setRepositoryMethodMarker(valueMap.get("repositoryMethodMarker"));
        config.setProjectType(valueMap.get("projectType"));

        return config;
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
