package org.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import org.sitoolkit.cv.core.domain.designdoc.Diagram;
import org.sitoolkit.cv.core.domain.uml.DiagramModel;

import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;

public abstract class PlantUmlUtil {

    public static <T extends DiagramModel> Diagram createDiagram(T model, Function<T,String> serializer) {
        Diagram diagram = new Diagram();
        diagram.setId(model.getId());
        diagram.setTags(model.getAllTags());

        SourceStringReader reader = new SourceStringReader(serializer.apply(model));

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            reader.outputImage(baos, new FileFormatOption(FileFormat.PNG));
            diagram.setData(baos.toByteArray());

            return diagram;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
