package io.sitoolkit.cv.core.domain.uml;

import lombok.Data;

@Data
public class MessageDef {
    private MessageType type = MessageType.SYNC;
    private String name;
    private LifeLineDef target;
}
