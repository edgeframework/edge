package org.edgeframework.daemon;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.edgeframework.daemon.internal.GradleBuilder;
import org.edgeframework.daemon.internal.Module;
import org.gradle.tooling.model.GradleProject;

import com.darylteo.nio.DirectoryWatcherFactory;

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
  private GradleProject project;

  public Daemon() throws IOException {
    setupBuildFiles();
    setupGradle();
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

  private void setupGradle() throws IOException {
    GradleBuilder builder = new GradleBuilder();

    new Module(builder.getProject(), builder, factory);

    for (GradleProject child : builder.getProject().getChildren()) {
      new Module(child, builder, factory);
    }
  }

};