package io.sitoolkit.cv.core.infra.lombok;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;
import lombok.launch.Delombok;

@Slf4j
public class Delomboker {

    public void execute(DelombokParameter param) {

        log.info("Delomboking {} ...", param.src.toAbsolutePath());

        try {
            Delombok delombok = new Delombok();
            delombok.addDirectory(param.src.toFile());
            if (param.target != null) {
                delombok.setOutput(param.target.toFile());
            }
            if (param.encoding != null) {
                delombok.setCharset(param.encoding);
            }
            if (param.classpath != null) {
                delombok.setClasspath( param.classpath.stream().map(Path::toAbsolutePath)
                .map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
            }
            if (param.sourcepath != null) {
                delombok.setSourcepath( param.sourcepath.stream().map(Path::toAbsolutePath)
                .map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
            }

            delombok.delombok();

            log.info("Delomboked {}", param.src.toAbsolutePath());

        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException
                | InvocationTargetException | IOException e) {
            log.info("Delombok failed : {}", param.src.toAbsolutePath(), e);
        }
    }
}


