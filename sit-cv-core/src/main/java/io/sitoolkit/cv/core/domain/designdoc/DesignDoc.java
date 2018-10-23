package io.sitoolkit.cv.core.domain.designdoc;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class DesignDoc {
    private String id;
    private String pkg;

    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Map<String, Diagram> map = new LinkedHashMap<>();

    public Diagram add(Diagram diagram) {
        return map.put(diagram.getId(), diagram);
    }

    public Collection<Diagram> getAllDiagrams() {
        return Collections.unmodifiableCollection(map.values());
    }

    public Set<String> getAllTags() {
        return getAllDiagrams().stream().map(Diagram::getTags).flatMap(Set::stream)
                .collect(Collectors.toSet());
    }
}
