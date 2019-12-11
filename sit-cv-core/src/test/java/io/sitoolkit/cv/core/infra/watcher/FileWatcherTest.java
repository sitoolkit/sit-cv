package io.sitoolkit.cv.core.infra.watcher;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.RandomStringUtils;
import org.awaitility.Awaitility;
import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileWatcherTest {

  FileWatcher watcher = new FileWatcher();
  Path baseDir;
  Duration TIMEOUT = Duration.ofMillis(5000);

  @Before
  public void setup() {
    baseDir = Paths.get("target", "filewatcher", RandomStringUtils.randomAlphabetic(5));
    baseDir.toFile().mkdirs();
    watcher.add(baseDir);

    assertThat(watcher.isWatching(baseDir), is(true));

    watcher.start();
  }

  @Test
  public void testWatching() throws IOException {

    Path newDir = Files.createTempDirectory(baseDir, "newDir");

    Awaitility.await().atMost(TIMEOUT).until(() -> watcher.isWatching(newDir));

    Path newFile = Files.createTempFile(newDir, "new", "file");

    Awaitility.await().atMost(TIMEOUT).until(() -> watcher.isWatching(newFile));
  }

  @Test
  public void testEventListener() throws IOException {
    FileWatchEventListenerImpl listener = new FileWatchEventListenerImpl();
    watcher.addListener(listener);

    Path newFile = Files.createTempFile(baseDir, "new", "file");

    Awaitility.await().atMost(TIMEOUT).until(() -> listener.contains(newFile));
  }

  @Test
  public void testEventListenerWithMultiFiles() throws IOException {
    FileWatchEventListenerImpl listener = new FileWatchEventListenerImpl();
    watcher.addListener(listener);

    List<Path> newFiles = List.of(Files.createTempFile(baseDir, "new", "file"));

    Awaitility.await()
        .atMost(TIMEOUT)
        .until(() -> newFiles.stream().allMatch(file -> listener.contains(file)));

    assertThat(listener.calledCount, is(1));
  }

  class FileWatchEventListenerImpl implements FileWatchEventListener {

    Set<Path> files = new HashSet<>();
    int calledCount = 0;

    @Override
    public void onModify(Set<Path> files) {
      this.files.clear();
      this.files.addAll(files);
      calledCount++;
    }

    boolean contains(Path file) {
      return this.files.contains(file.normalize().toAbsolutePath());
    }
  }
}
