package io.sitoolkit.cv.core.app.report;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.SystemUtils;

import io.sitoolkit.cv.core.domain.classdef.ClassDefReader;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepository;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryMemImpl;
import io.sitoolkit.cv.core.domain.classdef.ClassDefRepositoryParam;
import io.sitoolkit.cv.core.domain.classdef.MethodDef;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilter;
import io.sitoolkit.cv.core.domain.classdef.filter.ClassDefFilterConditionReader;
import io.sitoolkit.cv.core.domain.classdef.javaparser.ClassDefReaderJavaParserImpl;
import io.sitoolkit.cv.core.domain.designdoc.DesignDoc;
import io.sitoolkit.cv.core.domain.designdoc.Diagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagram;
import io.sitoolkit.cv.core.domain.uml.ClassDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.LifeLineDef;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagram;
import io.sitoolkit.cv.core.domain.uml.SequenceDiagramProcessor;
import io.sitoolkit.cv.core.domain.uml.plantuml.ClassDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.domain.uml.plantuml.PlantUmlWriter;
import io.sitoolkit.cv.core.domain.uml.plantuml.SequenceDiagramWriterPlantUmlImpl;
import io.sitoolkit.cv.core.infra.config.Config;
import io.sitoolkit.cv.core.infra.graphviz.GraphvizManager;
import net.sourceforge.plantuml.cucadiagram.dot.GraphvizUtils;
import io.sitoolkit.cv.core.domain.report.ReportWriter;

public class ReportService {
    private final String srcDirName = "src/main/java";
    private final String jarList = "sit-cv-jar-list.txt";
    private PlantUmlWriter plantumlWriter = new PlantUmlWriter();
    private ReportWriter reportWriter = new ReportWriter();
    private SequenceDiagramWriterPlantUmlImpl sequenceWriter = new SequenceDiagramWriterPlantUmlImpl();
    private SequenceDiagramProcessor sequenceProcessor = new SequenceDiagramProcessor();
    private ClassDiagramWriterPlantUmlImpl classWriter = new ClassDiagramWriterPlantUmlImpl();
    private ClassDiagramProcessor classProcessor = new ClassDiagramProcessor();

    public ReportService() {
        initGraphviz();
    }

    public void write() {
        write("./");
    }

    public void write(String prjDirName) {
        ClassDefRepository repository = getClassDefRepository(prjDirName, srcDirName);

        Set<String> designDocIds = repository.getEntryPoints();
        List<DesignDoc> designDocs = new ArrayList<>();
        designDocIds.stream().forEach((designDocId) -> {
            MethodDef entryPoint = repository.findMethodByQualifiedSignature(designDocId);

            DesignDoc doc = new DesignDoc();
            doc.setId(designDocId);
            doc.setPkg(entryPoint.getClassDef().getPkg());
            doc.add(getSequenceDiagram(entryPoint));
            doc.add(getClassDiagram(entryPoint));
            designDocs.add(doc);
        });

        reportWriter.write(designDocs, prjDirName);
    }

    private void initGraphviz() {
        if (SystemUtils.IS_OS_WINDOWS) {
            GraphvizManager graphvizManager = new GraphvizManager();
            String graphvizPath = graphvizManager.getBinaryPath().toAbsolutePath().toString();
            GraphvizUtils.setDotExecutable(graphvizPath);
        }
    }

    private ClassDefRepository getClassDefRepository(String prjDir, String srcDir) {
        ClassDefFilter filter = new ClassDefFilter();
        ClassDefFilterConditionReader.read(Paths.get(prjDir)).ifPresent(filter::setCondition);

        ClassDefRepositoryParam param = new ClassDefRepositoryParam();
        param.setProjectDir(Paths.get(prjDir));
        param.setSrcDirs(Arrays.asList(Paths.get(srcDir)));
        param.setJarPaths(Arrays.asList());
        param.setJarList(Paths.get(prjDir, jarList));
        param.setBinDirs(Arrays.asList());

        ClassDefRepository repository = new ClassDefRepositoryMemImpl();
        Config config = new Config();

        ClassDefReader reader = new ClassDefReaderJavaParserImpl(repository, config);
        reader.init(param);
        reader.readDir(Paths.get(srcDir));

        return repository;
    }

    private Diagram getSequenceDiagram(MethodDef entryPoint) {
        LifeLineDef entryLifeLine = sequenceProcessor.process(entryPoint.getClassDef(), entryPoint);
        SequenceDiagram sd = SequenceDiagram.builder().entryLifeLine(entryLifeLine).build();
        return plantumlWriter.createDiagram(sd, sequenceWriter::serialize);
    }

    private Diagram getClassDiagram(MethodDef entryPoint) {
        ClassDiagram cd = classProcessor.process(entryPoint);
        return plantumlWriter.createDiagram(cd, classWriter::serialize);
    }

}
