package io.sitoolkit.cv.core.infra.config;

import static java.util.stream.Collectors.toList;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonMerge;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import org.apache.commons.beanutils.BeanUtils;

@Data
public class CvConfig {

  @JsonIgnore private Path sourcePath;

  private String jarList = "jar-list.txt";
  private String javaFilePattern = ".*\\.(java|class)$";

  private boolean override = false;
  private boolean exception = true;
  private boolean showAccessor = false;

  @JsonMerge private List<LifelineClasses> lifelines = new ArrayList<>();
  private EnclosureFilterCondition sqlLogPattern;
  @JsonMerge private List<String> asyncAnnotations = new ArrayList<>();

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
    List<LifelineClasses> entryPoints =
        lifelines.stream().filter(LifelineClasses::isEntryPoint).collect(toList());
    return toFilterConditionGroup(entryPoints);
  }

  public FilterConditionGroup getLifelineOnlyFilter() {
    List<LifelineClasses> repositories =
        lifelines.stream().filter(LifelineClasses::isLifelineOnly).collect(toList());
    return toFilterConditionGroup(repositories);
  }

  public FilterConditionGroup getSequenceDiagramFilter() {
    return toFilterConditionGroup(lifelines);
  }

  public FilterConditionGroup getRepositoryFilter() {
    List<LifelineClasses> repositories =
        lifelines.stream().filter(LifelineClasses::isDbAccess).collect(toList());
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
    List<FilterCondition> include = new ArrayList<>();
    List<FilterCondition> exclude = new ArrayList<>();

    fcg.setInclude(include);
    fcg.setExclude(exclude);

    lifelines.forEach(
        lifeLine -> {
          include.add(lifeLine.getCondition());
          if (lifeLine.isExclude()) {
            exclude.add(lifeLine.getCondition());
          }
        });

    return fcg;
  }
}
