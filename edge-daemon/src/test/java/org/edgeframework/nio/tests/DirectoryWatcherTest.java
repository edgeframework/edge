package org.edgeframework.nio.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.edgeframework.nio.DirectoryWatcher;
import org.edgeframework.nio.DirectoryWatcherFactory;
import org.edgeframework.nio.DirectoryWatcherSubscriber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DirectoryWatcherTest {

  private DirectoryWatcherFactory factory;
  private DirectoryWatcher watcher;
  private Path root = Paths.get("watcher_test");

  private static final int LATCH_TIMEOUT = 15;

  @Before
  public void before() throws IOException {
    System.out.println("\nRunning Test");

    resetTestFolder(root);

    factory = new DirectoryWatcherFactory();
    watcher = factory.newWatcher(root);
  }

  @After
  public void after() throws Exception {
    factory.close();
  }

  public void resetTestFolder(Path root) throws IOException {
    if (Files.exists(root)) {
      Files.walkFileTree(root, new FileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          Files.delete(file);
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
          Files.delete(dir);
          return FileVisitResult.CONTINUE;
        }
      });
    }

    Files.createDirectories(root.resolve("empty1/empty2"));
    Files.createDirectories(root.resolve("level1/level2"));

    Files.createFile(root.resolve("file"));
    Files.createFile(root.resolve("level1/file"));
    Files.createFile(root.resolve("level1/level2/file"));
  }

  @Test
  public void testCreateDirectory1() throws InterruptedException, IOException {
    final CountDownLatch latch = new CountDownLatch(1);
    final Path newPath = root.resolve("newdir");

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryCreated(DirectoryWatcher watcher, Path dir) {
        assertEquals("Two absolute paths are not equal", newPath, dir);
        latch.countDown();
      }
    });

    Files.createDirectories(newPath);

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testCreateDirectory2() throws InterruptedException, IOException {
    final CountDownLatch latch = new CountDownLatch(2);
    final Set<Path> paths = new HashSet<>();
    paths.add(root.resolve("newdir1"));
    paths.add(root.resolve("newdir1/newdir2"));

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryCreated(DirectoryWatcher watcher, Path dir) {
        assertTrue("Watcher did not return a correct path", paths.remove(dir));
        latch.countDown();
      }
    });

    Files.createDirectories(root.resolve("newdir1/newdir2"));

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteDirectory1() throws InterruptedException, IOException {
    /* Basic Deletion Test */
    final CountDownLatch latch = new CountDownLatch(1);
    final Path deletePath = root.resolve("empty1/empty2");

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryDeleted(DirectoryWatcher watcher, Path dir) {
        assertEquals("Watcher did not return a correct path", deletePath, dir);
        latch.countDown();
      }
    });

    Files.delete(deletePath);

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteDirectory2() throws InterruptedException, IOException {
    /* Nested Deletion Test */
    final CountDownLatch latch = new CountDownLatch(2);

    final Set<Path> paths = new HashSet<>();
    paths.add(root.resolve("empty1/empty2"));
    paths.add(root.resolve("empty1"));

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryDeleted(DirectoryWatcher watcher, Path dir) {
        assertTrue("Watcher did not return a correct path", paths.remove(dir));
        latch.countDown();
      }
    });

    deleteFileTree(root.resolve("empty1"));

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteFile1() throws IOException, InterruptedException {
    /* Delete a file in root */
    final CountDownLatch latch = new CountDownLatch(1);
    final Path deletePath = root.resolve("file");

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(DirectoryWatcher watcher, Path file) {
        assertEquals("Watcher did not return a correct path", deletePath, file);
        latch.countDown();
      }
    });

    Files.delete(deletePath);

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteFile2() throws IOException, InterruptedException {
    /* Delete a file deep in hierarchy */
    final CountDownLatch latch = new CountDownLatch(1);
    final Path deletePath = root.resolve("level1/level2/file");

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(DirectoryWatcher watcher, Path file) {
        assertEquals("Watcher did not return a correct path", deletePath, file);
        latch.countDown();
      }
    });

    Files.delete(deletePath);

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteFile3() throws IOException, InterruptedException {
    /* Delete multiple files */
    final CountDownLatch latch = new CountDownLatch(1);
    final Set<Path> paths = new HashSet<>();
    paths.add(root.resolve("file"));
    paths.add(root.resolve("level1/file"));
    paths.add(root.resolve("level1/level2/file"));

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(DirectoryWatcher watcher, Path file) {
        assertTrue("Watcher did not return a correct path", paths.remove(file));
        latch.countDown();
      }
    });

    for (Path p : paths) {
      Files.delete(p);
    }

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testFileModified1() throws IOException, InterruptedException {
    /* Modify a single file in root */
    final CountDownLatch latch = new CountDownLatch(1);
    final Path modifyPath = root.resolve("file");

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileModified(DirectoryWatcher watcher, Path file) {
        assertEquals("Watcher did not return a correct path", modifyPath, file);
        latch.countDown();
      }
    });

    // Need this to give the polling mechanism time to hook into events
    Thread.sleep(1000);
    writeToFile(modifyPath);

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testFileModified2() throws IOException, InterruptedException {
    /* Modify multiple files */
    final CountDownLatch latch = new CountDownLatch(3);
    final Set<Path> paths = new HashSet<>();
    paths.add(root.resolve("file"));
    paths.add(root.resolve("level1/file"));
    paths.add(root.resolve("level1/level2/file"));

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileModified(DirectoryWatcher watcher, Path file) {
        assertTrue("Watcher did not return a correct path", paths.remove(file));
        latch.countDown();
      }
    });

    // Need this to give the polling mechanism time to hook into events
    Thread.sleep(1000);
    for (Path p : paths) {
      writeToFile(p);
    }

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  private void deleteFileTree(Path path) throws IOException {
    Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }

  private void writeToFile(Path path) throws IOException {
    Files.write(path, "Hello World!".getBytes());
  }
}