package org.sitoolkit.cv.core.domain.designdoc;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class Diagram {
    private String id;
    private byte[] data;
    private DiagramType type;
    private Set<String> tags = new HashSet<>();
}
