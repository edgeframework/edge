package org.edgeframework.nio;

import java.nio.file.Path;

public abstract class DirectoryChangedSubscriber extends DirectoryWatcherSubscriber {
  public abstract void directoryChanged(DirectoryWatcher watcher, Path path);

  @Override
  public void directoryCreated(DirectoryWatcher watcher, Path dir) {
    directoryChanged(watcher, dir);
  }

  @Override
  public void directoryDeleted(DirectoryWatcher watcher, Path dir) {
    directoryChanged(watcher, dir);
  }

  @Override
  public void fileCreated(DirectoryWatcher watcher, Path file) {
    directoryChanged(watcher, file);
  }

  @Override
  public void fileDeleted(DirectoryWatcher watcher, Path file) {
    directoryChanged(watcher, file);
  }

  @Override
  public void fileModified(DirectoryWatcher watcher, Path file) {
    directoryChanged(watcher, file);
  }
}
