package io.sitoolkit.cv.core.infra.config;

import static java.util.stream.Collectors.toList;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class CvConfig {

  @JsonIgnore
  private Path sourcePath;

  private String jarList = "jar-list.txt";
  private String javaFilePattern = ".*\\.(java|class)$";

  private boolean override = false;
  private boolean exception = true;
  @JsonMerge
  private List<LifelineClasses> lifelines = new ArrayList<>();
  private EnclosureFilterCondition sqlLogPattern;
  @JsonMerge
  private List<String> asyncAnnotations = new ArrayList<>();
  @JsonIgnore
  @Setter(AccessLevel.NONE)
  private List<CvConfigEventListener> eventListeners = new ArrayList<>();

  public void update(CvConfig other) {
    try {
      BeanUtils.copyProperties(this, other);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new IllegalStateException(e);
    }
  }

  public FilterConditionGroup getEntryPointFilter() {
    List<LifelineClasses> entryPoints = lifelines.stream().filter(LifelineClasses::isEntryPoint)
        .collect(toList());
    return toFilterConditionGroup(entryPoints);
  }

  public FilterConditionGroup getLifelineOnlyFilter() {
    List<LifelineClasses> repositories = lifelines.stream().filter(LifelineClasses::isLifelineOnly)
        .collect(toList());
    return toFilterConditionGroup(repositories);
  }

  public FilterConditionGroup getSequenceDiagramFilter() {
    return toFilterConditionGroup(lifelines);
  }

  public FilterConditionGroup getRepositoryFilter() {
    List<LifelineClasses> repositories = lifelines.stream().filter(LifelineClasses::isDbAccess)
        .collect(toList());
    return toFilterConditionGroup(repositories);
  }

  public EnclosureFilterCondition getSqlEnclosureFilter() {
    return sqlLogPattern;
  }

  public void addEventListener(CvConfigEventListener eventListener) {
    eventListeners.add(eventListener);
  }

  private FilterConditionGroup toFilterConditionGroup(List<LifelineClasses> lifelines) {
    FilterConditionGroup fcg = new FilterConditionGroup();
    fcg.setInclude(lifelines.stream().map(LifelineClasses::getCondition).collect(toList()));
    return fcg;
  }
}
