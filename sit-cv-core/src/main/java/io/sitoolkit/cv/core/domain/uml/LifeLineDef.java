package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import lombok.Data;

@Data
public class LifeLineDef {
    private String objectName;
    private String entryMessage;
    private String sourceId;
    private List<MessageDef> messages = new ArrayList<>();
    private List<SequenceElement> elements = new ArrayList<>();
    private String comment;

    public Set<String> getAllSourceIds() {
        Set<String> tags = messages.stream().map(MessageDef::getTarget)
                .map(LifeLineDef::getSourceId).collect(Collectors.toSet());
        tags.add(sourceId);
        return tags;
    }

    public Map<String, String> getAllComments() {
        Map<String, String> comments = new HashMap<>();
        getComments(comments, messages);
        return comments;
    }

    void getComments(Map<String, String> comments, List<MessageDef> messages) {
        messages.stream().forEach((message) -> {
            LifeLineDef target = message.getTarget();
            getComments(comments, target.getMessages());
            comments.put(message.getRequestQualifiedSignature(), target.getComment());
        });
    }
}
