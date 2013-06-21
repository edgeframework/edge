package org.edgeframework.nio;

import java.io.IOException;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.sun.nio.file.ExtendedWatchEventModifier;
import com.sun.nio.file.SensitivityWatchEventModifier;

public class DirectoryWatcher implements AutoCloseable {
  private final WatchService watchService = FileSystems.getDefault().newWatchService();
  private final WatcherThread watcher = new WatcherThread();

  private final List<DirectoryWatcherSubscriber> subscribers = new ArrayList<>();

  /* These keep track of which directories we're tracking */
  private Map<WatchKey, Path> keys = new HashMap<>();
  private Set<Path> watched = new HashSet<>();

  /* Used to filter files */
  private final List<Pattern> includes = new LinkedList<>();
  private final List<Pattern> excludes = new LinkedList<>();

  /* WatchService Modifiers */
  private boolean isFileTreeModifierSupported = true;
  private final WatchEvent.Modifier[] FILETREE_MODIFIERS = new WatchEvent.Modifier[] {
    ExtendedWatchEventModifier.FILE_TREE
  };
  private final WatchEvent.Modifier[] POLLING_MODIFIERS = new WatchEvent.Modifier[] {
    SensitivityWatchEventModifier.HIGH
  };
  private WatchEvent.Modifier[] modifiers = FILETREE_MODIFIERS;
  private WatchEvent.Kind<?> kinds[] = new WatchEvent.Kind<?>[] {
    StandardWatchEventKinds.ENTRY_CREATE,
    StandardWatchEventKinds.ENTRY_DELETE,
    StandardWatchEventKinds.ENTRY_MODIFY
  };

  /* Constructors */
  public DirectoryWatcher() throws IOException {
    watcher.start();
  }

  public DirectoryWatcher(Path... watchPaths) throws IOException {
    for (Path path : watchPaths) {
      addPath(path);
    }
    watcher.start();
  }

  /* Paths */
  public void addPath(Path path) throws IOException {
    path = path.toAbsolutePath();

    if (!Files.isDirectory(path)) {
      return;
    }

    // passed the FILE_TREE supported test
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        try {
          WatchKey key = dir.register(watchService, kinds, modifiers);
          keys.put(key, dir);
          watched.add(dir);
        } catch (UnsupportedOperationException e) {
          /* FILE_TREE modifier not supported */
          /* this code should only fire once since we modify the modifiers here */
          System.out.println("FILE_TREE modifier is not supported by this operating system. Switching to Polling");

          isFileTreeModifierSupported = false;
          modifiers = POLLING_MODIFIERS;

          addPath(dir);
          return FileVisitResult.TERMINATE;
        }

        return FileVisitResult.CONTINUE;
      }
    });
  }

  /* Subscriptions */
  public void subscribe(DirectoryWatcherSubscriber subscriber) {
    subscribers.add(subscriber);
  }

  public void unsubscribe(DirectoryWatcherSubscriber subscriber) {
    subscribers.remove(subscriber);
  }

  @Override
  public void close() throws Exception {
    watcher.stopWatching();
  }

  /*
   * Thread responsible for the watching logic
   */
  private class WatcherThread extends Thread {
    public boolean stop = false;

    @Override
    public void run() {
      while (!stop) {
        WatchKey key = null;
        try {
          key = watchService.take();
        } catch (InterruptedException e) {
          continue;
        }

        /* Get the directory that triggered the event */
        Path dir = keys.get(key);

        /* Poll the events and handle */
        for (WatchEvent<?> event : key.pollEvents()) {
          try {
            handleEvent(dir, (WatchEvent<Path>) event);
          } catch (IOException e) {
            e.printStackTrace();
            continue;
          }
        }

        /* Reset the Key to get more events later */
        if (!key.reset()) {
          keys.remove(key);
          watched.remove(dir);

          try {
            handleDirectoryDeleted(dir);
          } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }
      }
    }

    public void stopWatching() {
      this.stop = true;
      this.interrupt();
    }

    private void handleEvent(Path dir, WatchEvent<Path> event) throws IOException {
      WatchEvent.Kind<?> kind = event.kind();

      if (kind == StandardWatchEventKinds.OVERFLOW) {
        return;
      }

      Path path = dir.resolve(event.context()).toAbsolutePath();
      System.out.println(path);
      System.out.println(kind);

      /* ENTRY_CREATE */
      if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
        if (Files.isDirectory(path)) {
          handleDirectoryCreated(path);
          return;
        }

        handleFileCreated(path);
        return;
      }

      /* ENTRY_MODIFY */
      if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
        if (Files.isDirectory(path)) {
          // ignore
          return;
        }

        handleFileModified(path);
        return;
      }

      /* ENTRY_DELETE */
      if (watched.contains(path)) {
        // the deleted entry was one of the watched directories
        // We'll wait for the key to invalidate to screw around with the
        // watched dirs and keys
        return;
      }

      handleFileDeleted(path);
    }

    private void handleDirectoryDeleted(Path path) throws IOException {
      for (DirectoryWatcherSubscriber s : subscribers) {
        s.directoryDeleted(path);
      }

      return;
    }

    private void handleDirectoryCreated(Path path) throws IOException {
      // if a new dir is created we need to register it to our watcher
      // else inner events won't be tracked. In some cases, we may only
      // receive an event for the top level dir: any further nested dir
      // will not have any event as we haven't registered them. We'll
      // need to manually traverse and make sure we got them too.

      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          addPath(dir);

          for (DirectoryWatcherSubscriber s : subscribers) {
            s.directoryCreated(dir);
          }

          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          handleFileCreated(file);

          return FileVisitResult.CONTINUE;
        }
      });
    }

    private void handleFileCreated(Path path) throws IOException {
      if (!shouldTrack(path)) {
        return;
      }

      for (DirectoryWatcherSubscriber s : subscribers) {
        s.fileCreated(path);
      }
    }

    private void handleFileDeleted(Path path) {
      if (!shouldTrack(path)) {
        return;
      }

      for (DirectoryWatcherSubscriber s : subscribers) {
        s.fileDeleted(path);
      }
    }

    private void handleFileModified(Path path) {
      if (Files.isDirectory(path)) {
        // ignore this... the files are what we are tracking
        return;
      }

      if (!shouldTrack(path)) {
        return;
      }

      for (DirectoryWatcherSubscriber s : subscribers) {
        s.fileDeleted(path);
      }
    }

    private boolean shouldTrack(Path path) {
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
}