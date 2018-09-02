package org.sitoolkit.cv.core.domain.uml;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SequenceDiagram {
    private LifeLineDef entryLifeLine;

    public String getId() {
        return entryLifeLine.getEntryMessage();
    }

    public Set<String> getAllTags() {
        return entryLifeLine.getAllSourceIds();
    }
}
