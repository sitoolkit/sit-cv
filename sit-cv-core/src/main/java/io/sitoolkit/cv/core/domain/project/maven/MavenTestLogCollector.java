package io.sitoolkit.cv.core.domain.project.maven;

import java.nio.file.Path;
import java.nio.file.Paths;

import io.sitoolkit.cv.core.infra.util.CsvUtils;
import io.sitoolkit.util.buildtoolhelper.maven.MavenProject;

public class MavenTestLogCollector {

    public static void main(String[] args) {

        Path projectDir = Paths.get("../../dddsample-core").toAbsolutePath().normalize();

        MavenProject project = MavenProject.load(projectDir);

        SqlLogListener stdoutListener = new SqlLogListener();

        project.mvnw("test",
                "-DargLine=-javaagent:../sit-cv/sit-cv-tools/target/sit-cv-tools-1.0.0-beta.4-SNAPSHOT.jar")
                .stdout(stdoutListener).execute();

        CsvUtils.bean2csv(stdoutListener.getSqlLogs(),
                Paths.get("./target/sit-cv-repository-vs-sql.csv"));
    }

}
