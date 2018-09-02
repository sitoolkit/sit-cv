package org.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class LifeLineDef {
    private String objectName;
    private String entryMessage;
    private String sourceId;
    private List<MessageDef> messages = new ArrayList<>();

    public Set<String> getAllSourceIds() {
        Set<String> tags = messages.stream().map(MessageDef::getTarget)
                .map(LifeLineDef::getSourceId).collect(Collectors.toSet());
        tags.add(sourceId);
        return tags;
    }
}
