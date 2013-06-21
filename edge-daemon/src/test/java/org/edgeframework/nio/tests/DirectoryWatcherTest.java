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
import org.edgeframework.nio.DirectoryWatcherSubscriber;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DirectoryWatcherTest {

  private DirectoryWatcher watcher;
  private Path root = Paths.get("watcher_test");

  private static final int LATCH_TIMEOUT = 15;

  @Before
  public void before() throws IOException {
    watcher = new DirectoryWatcher();

    resetTestFolder(root);
    watcher.addPath(root);
  }

  @After
  public void after() throws Exception {
    watcher.close();
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
    final Path newPath = root.resolve("newdir").toAbsolutePath();

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryCreated(Path path) {
        assertEquals("Two absolute paths are not equal", newPath, path);
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
    paths.add(root.resolve("newdir1").toAbsolutePath());
    paths.add(root.resolve("newdir1/newdir2").toAbsolutePath());

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryCreated(Path path) {
        assertTrue("Watcher did not return a correct path", paths.remove(path));
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
    final Path deletePath = root.resolve("empty1/empty2").toAbsolutePath();

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryDeleted(Path path) {
        assertEquals("Watcher did not return a correct path", deletePath, path);
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
    paths.add(root.resolve("empty1/empty2").toAbsolutePath());
    paths.add(root.resolve("empty1").toAbsolutePath());

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void directoryDeleted(Path path) {
        assertTrue("Watcher did not return a correct path", paths.remove(path));
        latch.countDown();
      }
    });

    deleteFileTree(root.resolve("empty1"));

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteDirectory3() throws InterruptedException, IOException {
    /* Nested Deletion Test */
    final CountDownLatch latch = new CountDownLatch(4);
    final Set<Path> paths = new HashSet<>();
    paths.add(root.resolve("level1/level2").toAbsolutePath());
    paths.add(root.resolve("level1/level2/file").toAbsolutePath());

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(Path path) {
        System.out.println("File Deleted");
        assertTrue("Watcher did not return a correct path", paths.remove(path));
        latch.countDown();
      }

      @Override
      public void directoryDeleted(Path path) {
        assertTrue("Watcher did not return a correct path", paths.remove(path));
        latch.countDown();
      }
    });

    deleteFileTree(root.resolve("level1/level2"));

    latch.await(LATCH_TIMEOUT, TimeUnit.SECONDS);
    assertEquals("Test timed out", 0, latch.getCount());
  }

  @Test
  public void testDeleteFile1() throws IOException, InterruptedException {
    /* Delete a file in root */
    final CountDownLatch latch = new CountDownLatch(1);
    final Path deletePath = root.resolve("file").toAbsolutePath();

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(Path path) {
        assertEquals("Watcher did not return a correct path", deletePath, path);
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
    final Path deletePath = root.resolve("level1/level2/file").toAbsolutePath();

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(Path path) {
        assertEquals("Watcher did not return a correct path", deletePath, path);
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
    paths.add(root.resolve("file").toAbsolutePath());
    paths.add(root.resolve("level1/file").toAbsolutePath());
    paths.add(root.resolve("level1/level2/file").toAbsolutePath());

    watcher.subscribe(new DirectoryWatcherSubscriber() {
      @Override
      public void fileDeleted(Path path) {
        assertTrue("Watcher did not return a correct path", paths.remove(path));
        latch.countDown();
      }
    });

    for (Path p : paths) {
      Files.delete(p);
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

}