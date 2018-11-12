package io.sitoolkit.cv.core.infra.lombok;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Delomboker {

    StdoutListener lombokListener = new StdoutListener() {
        @Override
        public void nextLine(String line) {
            log.debug("lombok >> {}", line);
        }
    };

    public void execute(DelombokParameter param) {
        ProcessCommand command = new ProcessCommand()
                .currentDirectory(param.src)
                .command("java")
                .args(delombokCommandArgs(param))
                .stdout(lombokListener);

        log.info("Delomboking {} ...", param.src.toAbsolutePath());
        command.execute();
        log.info("Delomboked {}", param.src.toAbsolutePath());
    }

    String[] delombokCommandArgs(DelombokParameter param) {

        List<String> args = new ArrayList<>();
        args.add("-jar");
        args.add(LombokManager.getLombokPath().toString());
        args.add("delombok");
        args.add(param.src.toAbsolutePath().toString());

        if (param.target != null) {
            args.add("--target=" + param.target.toAbsolutePath().toString());
        }
        if (param.encoding != null) {
            args.add("--encoding=" + param.encoding);
        }
        if (param.classpath != null) {
            args.add("--classpath=" + param.classpath.stream().map(Path::toAbsolutePath)
            .map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
        }
        if (param.sourcepath != null) {
            args.add("--sourcepath=" + param.sourcepath.stream().map(Path::toAbsolutePath)
            .map(Path::toString).collect(Collectors.joining(File.pathSeparator)));
        }
        if (param.onlyChanged) {
            args.add("--onlyChanged" );
        }
        return args.toArray(new String[] {});
    }

}


