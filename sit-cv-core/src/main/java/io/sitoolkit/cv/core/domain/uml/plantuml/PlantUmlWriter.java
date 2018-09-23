package io.sitoolkit.cv.core.domain.uml.plantuml;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.SystemUtils;

import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.DiagramModel;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;

public class PlantUmlWriter {

    @Resource
    GraphvizManager graphvizManager;

    @PostConstruct
    public void init() {
        if (SystemUtils.IS_OS_WINDOWS) {
            String graphvizPath = graphvizManager.getBinaryPath().toAbsolutePath().toString();
            GraphvizUtils.setDotExecutable(graphvizPath);
        }
    }

    public <T extends DiagramModel> Diagram createDiagram(T model, Function<T, String> serializer) {
        Diagram diagram = new Diagram();
        diagram.setId(model.getId());
        diagram.setTags(model.getAllTags());

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
