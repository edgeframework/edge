package org.edgeframework.daemon.internal;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.gradle.tooling.model.GradleProject;

import com.darylteo.nio.DirectoryChangedSubscriber;
import com.darylteo.nio.DirectoryWatcher;
import com.darylteo.nio.DirectoryWatcherFactory;

public class Module {
  public Module(
    final GradleProject project,
    final GradleBuilder builder,
    final DirectoryWatcherFactory factory) throws IOException {
    // remove beginning :
    String path = project.getPath().substring(1);

    DirectoryWatcher watcher = factory.newWatcher(Paths.get(path));

    watcher.include("src/**");
    watcher.include("conf/**");
    watcher.include("build.gradle");
    watcher.include("gradle.properties");
    watcher.include("settings.gradle");

    watcher.subscribe(new DirectoryChangedSubscriber() {
      @Override
      public void directoryChanged(DirectoryWatcher watcher, Path path) {
        System.out.println(project.getPath() + " > Changed Detected: " + path);
        builder.scheduleTask(String.format("%s:%s", project.getPath(), "copyMod"));
      }
    });
  }
}