package io.sitoolkit.cv.core.infra.config;

import static java.util.stream.Collectors.toList;

import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class SitCvConfig {

  @JsonIgnore
  private URL sourceUrl;

  @JsonIgnore
  private Path sourcePath;

  private String jarList = "jar-list.txt";
  private String javaFilePattern = ".*\\.(java|class)$";

  private boolean override = false;
  private List<LifelineClasses> lifelines = new ArrayList<>();
  private EnclosureFilterCondition sqlLogPattern;

  private static SitCvConfig newInstance(SitCvConfig original) {
    SitCvConfig newInstance = new SitCvConfig();
    newInstance.sourceUrl = original.sourceUrl;
    newInstance.sourcePath = original.sourcePath;
    newInstance.jarList = original.jarList;
    newInstance.javaFilePattern = original.javaFilePattern;
    newInstance.override = original.override;
    newInstance.lifelines.addAll(original.lifelines);
    newInstance.sqlLogPattern = original.sqlLogPattern;
    return newInstance;
  }

  public static SitCvConfig merge(SitCvConfig overwritten, SitCvConfig overwriting) {
    if (overwriting.override) {
      return newInstance(overwriting);
    }
    SitCvConfig merged = newInstance(overwritten);
    merged.sourceUrl = overwriting.sourceUrl;
    merged.sourcePath = overwriting.sourcePath;
    merged.jarList = overwriting.jarList;
    merged.javaFilePattern = overwriting.javaFilePattern;
    merged.override = overwriting.override;
    if (overwriting.lifelines != null) {
      merged.lifelines.addAll(overwriting.lifelines);
    }
    if (overwriting.sqlLogPattern != null) {
      merged.sqlLogPattern = overwriting.sqlLogPattern;
    }
    return merged;
  }

  public void updateBy(SitCvConfig other) {
    this.sourceUrl = other.sourceUrl;
    this.sourcePath = other.sourcePath;
    this.jarList = other.jarList;
    this.javaFilePattern = other.javaFilePattern;
    this.override = other.override;
    this.lifelines = other.lifelines;
    this.sqlLogPattern = other.sqlLogPattern;
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

  private FilterConditionGroup toFilterConditionGroup(List<LifelineClasses> lifelines) {
    FilterConditionGroup fcg = new FilterConditionGroup();
    fcg.setInclude(lifelines.stream().map(LifelineClasses::getCondition).collect(toList()));
    return fcg;
  }
}
