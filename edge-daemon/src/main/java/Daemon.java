import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.edgeframework.nio.DirectoryWatcher;
import org.edgeframework.nio.DirectoryWatcherFactory;
import org.edgeframework.nio.DirectoryWatcherSubscriber;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ProjectConnection;

public class Daemon {
  public static void main(String args[]) throws IOException {
    Daemon daemon = new Daemon();

    synchronized (daemon) {
      try {
        daemon.wait();
      } catch (InterruptedException e) {
      }
    }
  }

  private DirectoryWatcherFactory factory = new DirectoryWatcherFactory();

  public Daemon() throws IOException {
    System.out.println("Running Daemon in: " + Paths.get("").toAbsolutePath());
    System.out.println("Creating Vertx Platform Manager");

    setupBuildFiles();
    setupDirectoryWatcher();
    setupGradleDaemon();
  }

  private void setupDirectoryWatcher() throws IOException {
    DirectoryWatcher watcher = factory.newWatcher(Paths.get("src"));

    watcher.subscribe(new DirectoryWatcherSubscriber() {
    });
  }

  private void setupBuildFiles() throws IOException {
    InputStream buildScriptStream = getClass().getResourceAsStream("edge/conf/build.gradle");
    Path buildScriptPath = Paths.get("build.gradle");
    Path settingsScriptPath = Paths.get("settings.gradle");
    Path confScriptPath = Paths.get("conf", "config.gradle");

    if (!Files.exists(buildScriptPath)) {
      Files.copy(buildScriptStream, buildScriptPath);
    }

    if (!Files.exists(settingsScriptPath)) {
      Files.createFile(settingsScriptPath);
    }

    if (!Files.exists(confScriptPath)) {
      Files.createFile(confScriptPath);
    }
  }

  private void setupGradleDaemon() {
    ProjectConnection conn = GradleConnector.newConnector().forProjectDirectory(new File("")).connect();

    conn
      .newBuild()
      .setJvmArguments("-cp", "../bin")
      .forTasks("copyToMods")
      .run();

    conn.close();
  }
};