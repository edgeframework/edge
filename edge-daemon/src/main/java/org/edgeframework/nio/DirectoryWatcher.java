package org.edgeframework.nio;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchKey;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class DirectoryWatcher {
  private final List<DirectoryWatcherSubscriber> subscribers = new ArrayList<>();

  private Path path;

  /* Used to filter files */
  private final List<Pattern> includes = new LinkedList<>();
  private final List<Pattern> excludes = new LinkedList<>();

  /* Used to determine watch status */
  private final Set<WatchKey> dirs = new HashSet<>();

  /* Constructors */
  DirectoryWatcher(Path path) throws IOException {
    this.path = path;
  }

  /* Properties */
  public Path getPath() {
    return this.path;
  }

  List<DirectoryWatcherSubscriber> getSubscribers() {
    return this.subscribers;
  }

  /* Subscriptions */
  public void subscribe(DirectoryWatcherSubscriber subscriber) {
    subscribers.add(subscriber);
  }

  public void unsubscribe(DirectoryWatcherSubscriber subscriber) {
    subscribers.remove(subscriber);
  }

  /* WatchKeys */
  boolean watch(WatchKey key) {
    return this.dirs.add(key);
  }

  boolean unwatch(WatchKey key) {
    return this.dirs.remove(key);
  }

  boolean isWatching(WatchKey key) {
    return dirs.contains(key);
  }

  /* Pattern Filters */
  boolean shouldTrack(Path path) {
    return shouldInclude(path) && !shouldExclude(path);
  }

  private boolean shouldInclude(Path path) {
    for (Pattern pattern : includes) {
      // TODO:
    }

    return true;
  }

  private boolean shouldExclude(Path path) {
    for (Pattern pattern : excludes) {
      // TODO:
    }

    return false;
  }

}