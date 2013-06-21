package org.edgeframework.nio;

import java.nio.file.Path;

public abstract class DirectoryWatcherSubscriber {
  public void directoryCreated(Path path) {
  }

  public void directoryDeleted(Path path) {
  }

  public void fileCreated(Path path) {
  }

  public void fileDeleted(Path path) {
  }

  public void fileModified(Path path) {
  }
}
