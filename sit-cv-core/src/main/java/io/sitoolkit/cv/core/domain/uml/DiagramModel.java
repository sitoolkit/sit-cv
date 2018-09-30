package io.sitoolkit.cv.core.domain.uml;

import java.util.Map;
import java.util.Set;

public interface DiagramModel {
    public String getId();
    public Set<String> getAllTags();
    public Map<String, String> getAllComments();
}
