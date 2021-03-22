package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.util.Set;

import org.junit.Test;

import io.sitoolkit.cv.core.domain.uml.MessageDef;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SequenceDiagramWriterPlantUmlImplTest {

    static SequenceDiagramWriterPlantUmlImpl instance = new SequenceDiagramWriterPlantUmlImpl(
        new PlantUmlWriter());

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
