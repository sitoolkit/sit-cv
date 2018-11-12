package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.DiagramModel;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public class PlantUmlWriter {
    public final String LINE_SEPARATOR = "\\n\\\n";

    public <T extends DiagramModel> Diagram createDiagram(T model, Function<T, String> serializer) {
        Diagram diagram = new Diagram();
        diagram.setId(model.getId());
        diagram.setTags(model.getAllTags());
        diagram.setApiDocs(model.getAllApiDocs());

        SourceStringReader reader = new SourceStringReader(serializer.apply(model));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            reader.outputImage(baos, new FileFormatOption(FileFormat.SVG));
            diagram.setData(baos.toByteArray());

            return diagram;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
