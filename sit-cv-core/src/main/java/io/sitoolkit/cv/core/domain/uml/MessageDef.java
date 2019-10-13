package io.sitoolkit.cv.core.domain.uml;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.TypeDef;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class MessageDef extends SequenceElement {
  private MessageType type = MessageType.SYNC;
  private String requestName;
  private List<TypeDef> requestParamTypes = new ArrayList<>();
  private String requestQualifiedSignature;
  private LifeLineDef target;
  private TypeDef responseType;
  private MethodDef methodDef;
  private boolean isAsync;

  @Override
  public List<String> write(LifeLineDef lifeLine, SequenceElementWriter writer) {
    return writer.write(lifeLine, this);
  }

  @Override
  public Stream<MessageDef> getMessagesRecursively() {
    Stream<MessageDef> messages = getTarget().getMessagesRecursively();
    return Stream.concat(Stream.of(this), messages);
  }
}
