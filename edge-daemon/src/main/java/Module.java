import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

public class Module {
  private Path[] sources;
  private Path destination;

  public Module(String name) {
    this.sources = new Path[] {
        Paths.get("src", name, "java"),
        Paths.get("src", name, "groovy"),
        Paths.get("src", name, "scala"),
        Paths.get("src", name, "resources")
    };
    this.destination = Paths.get("mods", String.format("vertx~daemon~%s", name));

    try {
      setup();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void setup() throws IOException {
    /* Clean up the destination when setting up */
    deleteDirectory(destination);

    /* Walk source files */
    Set<Path> sourceFiles = walkFileTree();

  }

  private Set<Path> walkFileTree() throws IOException {
    final Set<Path> visited = new HashSet<>();

    for (final Path path : sources) {
      Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
          return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
          visited.add(path.relativize(file));
          return FileVisitResult.CONTINUE;
        }
      });
    }

    return visited;
  }

  private void copyDirectory(final Path from, final Path to) throws IOException {
    if (!Files.exists(from)) {
      return;
    }
    if (!Files.exists(to)) {
      Files.createDirectories(to);
    }

    Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println("Copy: " + file);

        Path filepath = from.relativize(file);
        Files.copy(file, to.resolve(filepath));

        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
        if (dir.equals(from)) {
          return FileVisitResult.CONTINUE;
        }

        System.out.println("Copy: " + dir);
        Path filepath = from.relativize(dir);
        Files.copy(dir, to.resolve(filepath));

        return FileVisitResult.CONTINUE;
      }
    });
  }

  private void deleteDirectory(Path dir) throws IOException {
    if (!Files.exists(dir)) {
      return;
    }

    Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        System.out.println("Delete: " + file);
        Files.delete(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
        System.out.println("Delete: " + dir);
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
      }
    });
  }
}
