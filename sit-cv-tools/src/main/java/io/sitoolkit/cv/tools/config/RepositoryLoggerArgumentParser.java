package io.sitoolkit.cv.tools.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RepositoryLoggerArgumentParser {

    private static final String ITEM_SEPARATOR = ";";
    private static final String KEY_VALUE_SEPARATOR = "=";
    private static final String VALUE_SEPARATOR = ",";

    public RepositoryLoggerConfig parse(String args) {
        Map<String, String> valueMap = new HashMap<>();
        for (String item : args.split(ITEM_SEPARATOR)) {
            String[] keyValue = item.split(KEY_VALUE_SEPARATOR);

            String key = keyValue[0];
            String value = keyValue.length == 1 ? null : keyValue[1];
            valueMap.put(key, value);
        }

        RepositoryLoggerConfig config = new RepositoryLoggerConfig();
        config.filterConditions = splitValue(valueMap.get("repository.annotation")).stream()
                .map(RepositoryFilterCondition::new).collect(Collectors.toList());
        config.methodMarker = valueMap.get("repository.methodMarker");

        return config;
    }

    private List<String> splitValue(String value) {
        if (value == null) {
            return Collections.emptyList();
        } else {
            return Arrays.asList(value.split(VALUE_SEPARATOR));
        }
    }
}
