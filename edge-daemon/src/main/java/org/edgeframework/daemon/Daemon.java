package org.edgeframework.daemon;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.edgeframework.daemon.internal.GradleBuilder;
import org.edgeframework.daemon.internal.Module;
import org.gradle.tooling.model.GradleProject;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

import com.darylteo.nio.DirectoryWatcherFactory;

public class Daemon {
  public static void main(String[] args) throws IOException {
    new Daemon(args);
  }

  private DirectoryWatcherFactory factory = new DirectoryWatcherFactory();
  private GradleProject project;

  public Daemon(String[] args) throws IOException {
    if (args.length == 0) {
      displayHelp();
    } else {
      if (args[0].equals("init")) {
        initBuildFiles();
      } else if (args[0].equals("run")) {
        initDaemon();
      } else {
        displayHelp();
      }
    }

    System.exit(0);
  }

  private void displayHelp() {
    System.out.println("Edge Help - please use the following commands:   ");
    System.out.println(" init - initialises a new Edge/Vert.x project    ");
    System.out.println(" run  - starts a daemon for a Edge/Vert.x project");
  }

  private void initBuildFiles() throws IOException {
    if (!isEmpty(Paths.get(""))) {
      System.err.println("Current directory is not empty and cannot be initialised.");
      System.exit(1);
    }

    InputStream buildScriptStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("edge/conf/build.gradle");
    Path buildScriptPath = Paths.get("build.gradle");
    Path settingsScriptPath = Paths.get("settings.gradle");
    Path confScriptPath = Paths.get("conf", "config.gradle");

    Files.copy(buildScriptStream, buildScriptPath);
    Files.createFile(settingsScriptPath);

    Files.createDirectories(Paths.get("conf"));
    Files.createFile(confScriptPath);
  }

  private boolean isEmpty(Path path) {
    return path.toAbsolutePath().toFile().listFiles(new FileFilter() {
      @Override
      public boolean accept(File pathname) {
        System.out.println(pathname);
        return !pathname.isHidden();
      }
    }).length > 0;
  }

  private boolean isInitialised() throws IOException {
    return Files.exists(Paths.get("build.gradle")) && Files.exists(Paths.get("settings.gradle")) && Files.exists(Paths.get("conf", "config.gradle"));
  }

  private void initDaemon() throws IOException {
    if (!isInitialised()) {
      System.out.println("This does not seem to be an initialised Edge project. Please run 'edge init' ");
      System.exit(1);
    }

    PlatformManager manager = PlatformLocator.factory.createPlatformManager();
    GradleBuilder builder = new GradleBuilder();

    new Module(builder.getProject(), builder, factory);

    for (GradleProject child : builder.getProject().getChildren()) {
      new Module(child, builder, factory);
    }

    synchronized (this) {
      try {
        this.wait();
      } catch (InterruptedException e) {
      }
    }
  }

};