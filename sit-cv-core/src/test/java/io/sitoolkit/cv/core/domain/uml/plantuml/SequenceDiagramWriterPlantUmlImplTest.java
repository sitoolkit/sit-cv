package io.sitoolkit.cv.core.domain.uml.plantuml;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import io.sitoolkit.cv.core.domain.uml.MessageDef;
import java.util.Set;
import org.junit.jupiter.api.Test;

public class SequenceDiagramWriterPlantUmlImplTest {

  static SequenceDiagramWriterPlantUmlImpl instance =
      new SequenceDiagramWriterPlantUmlImpl(new PlantUmlWriter());

  @Test
  public void linefeedInException() {

    MessageDef messageDef = new MessageDef();
    messageDef.setExceptions(Set.of("new Exception(\nlinefeed)"));

    String builtComment = instance.buildExceptionComment(messageDef);

    assertThat(builtComment, is("new Exception(\\nlinefeed)"));
  }

  @Test
  public void carriageReturnInException() {

    MessageDef messageDef = new MessageDef();
    messageDef.setExceptions(Set.of("new Exception(\rcarriageReturn)"));

    String builtComment = instance.buildExceptionComment(messageDef);

    assertThat(builtComment, is("new Exception(\\ncarriageReturn)"));
  }

  @Test
  public void crlfInException() {

    MessageDef messageDef = new MessageDef();
    messageDef.setExceptions(Set.of("new Exception(\r\ncrlf)"));

    String builtComment = instance.buildExceptionComment(messageDef);

    assertThat(builtComment, is("new Exception(\\ncrlf)"));
  }

  @Test
  public void allLineFeedsInException() {

    MessageDef messageDef = new MessageDef();
    messageDef.setExceptions(Set.of("new Exception(\r\ncrlf,\nlinefeed2,\rcarriageReturn)"));

    String builtComment = instance.buildExceptionComment(messageDef);

    assertThat(builtComment, is("new Exception(\\ncrlf,\\nlinefeed2,\\ncarriageReturn)"));
  }
}
