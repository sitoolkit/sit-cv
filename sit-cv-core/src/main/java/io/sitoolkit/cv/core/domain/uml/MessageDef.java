package io.sitoolkit.cv.core.domain.uml;

import lombok.Data;

@Data
public class MessageDef {
    private MessageType type = MessageType.SYNC;
    private String requestName;
    private LifeLineDef target;
    private String responseName;
}
