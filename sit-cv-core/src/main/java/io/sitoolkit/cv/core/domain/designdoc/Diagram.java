package io.sitoolkit.cv.core.domain.designdoc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Data;

@Data
public class Diagram {
    private String id;
    private byte[] data;
    private DiagramType type;
    private Set<String> tags = new HashSet<>();
    private Map<String, String> comments = new HashMap<>();
}
