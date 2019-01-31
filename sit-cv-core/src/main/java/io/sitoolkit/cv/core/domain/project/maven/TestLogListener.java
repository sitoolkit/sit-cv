package io.sitoolkit.cv.core.domain.project.maven;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.UncheckedIOException;

import io.sitoolkit.util.buildtoolhelper.process.StdoutListener;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestLogListener implements StdoutListener {

    @NonNull
    private BufferedWriter writer;

    @Override
    public void nextLine(String line) {
        try {
            writer.append(line);
            writer.append("\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
