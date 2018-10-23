package io.sitoolkit.cv.plugin.maven;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.cv.core.infra.SitRepository;
import io.sitoolkit.util.buidtoolhelper.maven.MavenProject;
import io.sitoolkit.util.buidtoolhelper.process.ProcessCommand;
import io.sitoolkit.util.buidtoolhelper.process.ProcessConversation;
import io.sitoolkit.util.buidtoolhelper.process.ProcessExitCallback;
import io.sitoolkit.util.buidtoolhelper.process.StdoutListener;

@Mojo(name = "run")
public class RunApplicationMojo
        extends AbstractMojo {

    @Parameter(defaultValue = "${plugin.groupId}")
    String cvAppGroupId;

    @Parameter(defaultValue = "sit-cv-app")
    String cvAppArtifactId;

    @Parameter(defaultValue = "${plugin.version}")
    String cvAppVersion;

    @Parameter(defaultValue = "30")
    int cvAppStartWaitingSec;

    @Parameter(defaultValue = "${project.basedir}")
    File projectDir;

    static final int APP_START_LISTENING_INTERVAL_MILLI = 2000;

    @Override
    public void execute()
            throws MojoExecutionException {
                Path executableJar = prepareExecutableJar();
                startApp(executableJar);
    }

    Path prepareExecutableJar() throws MojoExecutionException {

        Path repository = SitRepository.getRepositoryPath().resolve("sit-cv").resolve(cvAppArtifactId);
        String jarName = String.format("%s-%s.jar", cvAppArtifactId, cvAppVersion);
        String artifact = String.format("%s:%s:%s", cvAppGroupId, cvAppArtifactId, cvAppVersion);

        StdoutListener listener = new StdoutListener() {
            @Override
            public void nextLine(String line) {
                getLog().debug("mvnw copy >>" + line);
            }
        };

        getLog().info("copying SIT-CV-App jar file...");

        MavenProject.load(projectDir.toPath())
                .mvnw("dependency:copy",
                        "-DoutputDirectory=" + repository.toAbsolutePath().toString(),
                        "-Dartifact=" + artifact)
                .stdout(listener)
                .execute();

        Path jarPath = repository.resolve(jarName);

        if (Files.exists(jarPath)) {
            getLog().info("SIT-CV-App jar file copyed");
            return jarPath;

        } else {
            getLog().error("copying SIT-CV-App jar file failed");
            throw new MojoExecutionException("copying SIT-CV-App jar file failed");
        }
    }

    void startApp(Path executableJar) throws MojoExecutionException {

        CvAppListener listener = new CvAppListener();
        ProcessCommand sitCommand = new ProcessCommand()
                .currentDirectory(projectDir.toPath())
                .command("java")
                .args("-jar",
                        executableJar.toAbsolutePath().toString())
                .stdout(listener);
        sitCommand.getExitCallbacks().add(listener);

        getLog().info("Starting SIT-CV-App ...");
        ProcessConversation processConversation = sitCommand.executeAsync();
        Instant executedTime = Instant.now();

        while (listener.getAppState() == AppState.STARTING &&
                Instant.now().isBefore(executedTime.plusSeconds(cvAppStartWaitingSec))) {
            try {
                TimeUnit.MILLISECONDS.sleep(APP_START_LISTENING_INTERVAL_MILLI);
            } catch (InterruptedException e) {
                throw new MojoExecutionException("Waiting app start is interrupted", e);
            }
        }

        switch (listener.getAppState()) {
        case STARTED:
            getLog().info("SIT-CV-App started");
            return;

        case STARTING:
            getLog().error("SIT-CV-App start failed : timeout - " + cvAppStartWaitingSec + " seconds");
            shutdown(processConversation);
            throw new MojoExecutionException("SIT-CV-App start failed : timeout");

        case EXITED:
            getLog().error("SIT-CV-App start failed : exit code = " + listener.exitCode);
            throw new MojoExecutionException("SIT-CV-App start failed");
        }
    }

    void shutdown(ProcessConversation processConversation) throws MojoExecutionException{
        getLog().info("Shutting down SIT-CV-App ...");
        processConversation.getProcess().destroyForcibly();
        processConversation.destroy();
        getLog().info("SIT-CV-App was shut down");
    }

    class CvAppListener implements StdoutListener, ProcessExitCallback {
        boolean isAppStarted = false;
        boolean isAppExited = false;
        int exitCode = 0;

        @Override
        public void nextLine(String line) {
            if (!isAppStarted) {
                getLog().debug("starting SIT-CV-App >>" + line);
                if (line.contains("Started SitCvApplication")) {
                    getLog().debug("SIT-CV Started");
                    isAppStarted = true;
                }
            }
        }

        @Override
        public void callback(int exitCode) {
            getLog().debug("SIT-CV-App Exited");
            isAppExited = true;
            this.exitCode = exitCode;
        }

        public AppState getAppState() {
            return isAppExited ? AppState.EXITED
                    : isAppStarted ? AppState.STARTED
                            : AppState.STARTING;
        }
    };

    enum AppState {
        STARTING, STARTED, EXITED
    }
}
