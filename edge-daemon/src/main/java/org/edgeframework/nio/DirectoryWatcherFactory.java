package org.edgeframework.nio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.nio.file.ExtendedWatchEventModifier;
import com.sun.nio.file.SensitivityWatchEventModifier;

public class DirectoryWatcherFactory implements AutoCloseable {
  private final WatchService watchService = FileSystems.getDefault().newWatchService();
  private final ExecutorService executorService = Executors.newCachedThreadPool();

  /* WatchService Modifiers */
  private boolean isFileTreeModifierSupported = true;
  private final WatchEvent.Modifier[] FILETREE_MODIFIERS = new WatchEvent.Modifier[] {
    ExtendedWatchEventModifier.FILE_TREE
  };
  private final WatchEvent.Modifier[] POLLING_MODIFIERS = new WatchEvent.Modifier[] {
    SensitivityWatchEventModifier.HIGH
  };
  private WatchEvent.Modifier[] modifiers = FILETREE_MODIFIERS;
  private WatchEvent.Kind<?> WATCH_EVENTS[] = new WatchEvent.Kind<?>[] {
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_DELETE,
    StandardWatchEventKinds.ENTRY_MODIFY
  };

  private Set<Path> dirs = new HashSet<>();

  /* Watcher Mapping */
  private List<DirectoryWatcher> watchers = new LinkedList<>();

  public DirectoryWatcherFactory() throws IOException {
    this(1);
  }

  public DirectoryWatcherFactory(int threadCount) throws IOException {
    for (int i = 0; i < threadCount; i++) {
      executorService.execute(new WatcherThread());
    }
  }

  public DirectoryWatcher newWatcher(Path dir) throws IOException {
    DirectoryWatcher watcher = new DirectoryWatcher(dir);
    addWatcher(watcher);

    return watcher;
  }

  @Override
  public void close() throws Exception {
    watchService.close();
    executorService.shutdown();
  }

  /* Private Methods */
  private void addWatcher(final DirectoryWatcher watcher) throws IOException {
    Path path = watcher.getPath();

    if (!Files.exists(path)) {
      throw new FileNotFoundException(path.toString());
    }

    // passed the FILE_TREE supported test
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        registerPath(watcher, dir);
        dirs.add(dir);
        return FileVisitResult.CONTINUE;
      }
    });

    watchers.add(watcher);
  }

  private boolean registerPath(DirectoryWatcher watcher, Path path) throws IOException {
    if (!watcher.shouldTrack(path)) {
      return false;
    }

    try {
      WatchKey key = path.register(watchService, WATCH_EVENTS, modifiers);
      watcher.watch(key);

      return true;
    } catch (UnsupportedOperationException e) {
      /* FILE_TREE modifier not supported */
      /* this code should only fire once since we modify the modifiers here */
      isFileTreeModifierSupported = false;
      modifiers = POLLING_MODIFIERS;

      return registerPath(watcher, path);
    }
  }

  /*
   * Thread responsible for the watching logic
   */
  private class WatcherThread implements Runnable {
    @Override
    public void run() {
      while (true) {
        WatchKey key = null;
        try {
          key = watchService.take();
        } catch (InterruptedException | ClosedWatchServiceException e) {
          return;
        }

        /* Poll the events and handle */
        for (WatchEvent<?> event : key.pollEvents()) {
          try {
            handleEvent(key, (WatchEvent<Path>) event);
          } catch (IOException e) {
            continue;
          }
        }

        /* Reset the Key to get more events later */
        if (!key.reset()) {
          try {
            handleDirectoryDeleted(key, (Path) key.watchable());
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }

    private void handleEvent(WatchKey key, WatchEvent<Path> event) throws IOException {
      WatchEvent.Kind<?> kind = event.kind();

      if (kind == StandardWatchEventKinds.OVERFLOW) {
        return;
      }

      Path dir = (Path) key.watchable();
      Path path = dir.resolve(event.context());

      System.out.println(this + " > " + kind + " Received. Root: " + dir + " Path: " + path);

      /* ENTRY_CREATE */
      if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
        if (Files.isDirectory(path)) {
          handleDirectoryCreated(key, path);
          return;
        }

        handleFileCreated(key, path);
        return;
      }

      /* ENTRY_MODIFY */
      if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
        if (Files.isDirectory(path)) {
          // ignore
          return;
        }

        handleFileModified(key, path);
        return;
      }

      /* ENTRY_DELETE */
      if (dirs.contains(path)) {
        // the deleted entry was one of the watched directories
        // We'll wait for the key to invalidate to screw around with the
        // watched dirs and keys
        return;
      }

      handleFileDeleted(key, path);
    }

    private void handleDirectoryDeleted(WatchKey key, Path dir) throws IOException {
      for (DirectoryWatcher watcher : watchers) {
        if (!watcher.isWatching(key)) {
          continue;
        }

        dirs.remove(dir);

        for (DirectoryWatcherSubscriber s : watcher.getSubscribers()) {
          s.directoryDeleted(watcher, dir);
        }
      }

      return;
    }

    private void handleDirectoryCreated(final WatchKey key, Path dir) throws IOException {
      // if a new dir is created we need to register it to our watcher
      // else inner events won't be tracked. In some cases, we may only
      // receive an event for the top level dir: any further nested dir
      // will not have any event as we haven't registered them. We'll
      // need to manually traverse and make sure we got them too.
      Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          for (DirectoryWatcher watcher : watchers) {
            if (!watcher.isWatching(key)) {
              continue;
            }

            registerPath(watcher, dir);

            for (DirectoryWatcherSubscriber s : watcher.getSubscribers()) {
              s.directoryCreated(watcher, dir);
            }
          }

          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          handleFileCreated(key, file);

          return FileVisitResult.CONTINUE;
        }
      });
    }

    private void handleFileCreated(WatchKey key, Path file) throws IOException {
      for (DirectoryWatcher watcher : watchers) {
        if (!watcher.isWatching(key) || !watcher.shouldTrack(file)) {
          continue;
        }

        for (DirectoryWatcherSubscriber s : watcher.getSubscribers()) {
          s.fileCreated(watcher, file);
        }
      }
    }

    private void handleFileDeleted(WatchKey key, Path file) {
      for (DirectoryWatcher watcher : watchers) {
        if (!watcher.isWatching(key) || !watcher.shouldTrack(file)) {
          continue;
        }

        for (DirectoryWatcherSubscriber s : watcher.getSubscribers()) {
          s.fileDeleted(watcher, file);
        }
      }
    }

    private void handleFileModified(WatchKey key, Path file) {
      for (DirectoryWatcher watcher : watchers) {
        if (!watcher.isWatching(key) || !watcher.shouldTrack(file)) {
          continue;
        }

        for (DirectoryWatcherSubscriber s : watcher.getSubscribers()) {
          s.fileModified(watcher, file);
        }
      }
    }
  }
}
