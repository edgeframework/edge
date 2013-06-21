package org.edgeframework.nio;

import java.nio.file.Path;

public abstract class DirectoryWatcherSubscriber {
  public void directoryCreated(DirectoryWatcher watcher, Path dir) {
  }

  public void directoryDeleted(DirectoryWatcher watcher, Path dir) {
  }

  public void fileCreated(DirectoryWatcher watcher, Path file) {
  }

  public void fileDeleted(DirectoryWatcher watcher, Path file) {
  }

  public void fileModified(DirectoryWatcher watcher, Path file) {
  }
}
