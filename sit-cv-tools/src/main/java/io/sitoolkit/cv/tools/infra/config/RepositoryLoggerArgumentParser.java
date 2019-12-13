package io.sitoolkit.cv.tools.infra.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RepositoryLoggerArgumentParser {

  private static final String ITEM_SEPARATOR = ";";
  private static final String KEY_VALUE_SEPARATOR = "=";

  public RepositoryLoggerConfig parse(String args) {
    Map<String, String> valueMap = parseArgs(args);

    RepositoryLoggerConfig config = new RepositoryLoggerConfig();
    config.setRepositoryFilter(getFilter(valueMap, "repositoryFilter"));
    config.setEntrypointFilter(getFilter(valueMap, "entrypointFilter"));
    config.setRepositoryMethodMarker(valueMap.get("repositoryMethodMarker"));
    config.setProjectType(valueMap.get("projectType"));

    return config;
  }

  private FilterConditionGroup getFilter(Map<String, String> valueMap, String type) {
    FilterConditionGroup fcg = new FilterConditionGroup();
    List<FilterCondition> include = new ArrayList<>();
    List<FilterCondition> exclude = new ArrayList<>();
    fcg.setInclude(include);
    fcg.setExclude(exclude);

    int index = 0;
    Optional<FilterCondition> gotIncludeFilter;
    Optional<FilterCondition> gotExcludeFilter;
    do {
      index++;
      gotIncludeFilter = getSingleFilter("include.", valueMap, index, type);
      gotIncludeFilter.ifPresent(include::add);

      gotExcludeFilter = getSingleFilter("exclude.", valueMap, index, type);
      gotExcludeFilter.ifPresent(exclude::add);

    } while (gotIncludeFilter.isPresent() || gotExcludeFilter.isPresent());

    return fcg;
  }

  private Optional<FilterCondition> getSingleFilter(
      String prefix, Map<String, String> valueMap, int index, String type) {
    String annotation = valueMap.get(prefix + type + index + ".annotation");
    String name = valueMap.get(prefix + type + index + ".name");
    if (annotation == null && name == null) {
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
}
