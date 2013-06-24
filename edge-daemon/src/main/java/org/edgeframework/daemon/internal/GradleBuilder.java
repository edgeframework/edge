package org.edgeframework.daemon.internal;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;

public class GradleBuilder {

  private Path path;
  private ProjectConnection gradleConnection;
  private GradleProject project;
  private BuildLauncher builder;

  /* Used to stagger builds */
  private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
  private HashMap<String, Task> tasks = new HashMap<>();

  public GradleBuilder() {
    this(Paths.get(""));
  }

  public GradleBuilder(Path path) {
    this.path = path;

    gradleConnection = GradleConnector.newConnector().forProjectDirectory(new File("")).connect();
    ModelBuilder<GradleProject> builder = gradleConnection.model(GradleProject.class);

    /* Configure ModelBuilder */

    /* Get Model */
    this.project = builder.get();
    this.builder = gradleConnection.newBuild();
  }

  public GradleProject getProject() {
    return this.project;
  }

  public void scheduleTask(final String command) {
    Task task = tasks.get(command);

    if (task != null) {
      task.delay();
      return;
    }

    tasks.put(command, new Task(new Runnable() {
      @Override
      public void run() {
        System.out.println("Executing Command: " + command);
        tasks.remove(command);
        builder.forTasks(command).run();
      }
    }));
  }

  private class Task {
    private Runnable task;
    private ScheduledFuture<?> future;

    public Task(Runnable task) {
      this.task = task;
      schedule(task);
    }

    public void delay() {
      this.future.cancel(false);
      schedule(task);
    }

    private void schedule(Runnable task) {
      this.future = executor.schedule(task, 3, TimeUnit.SECONDS);
    }
  }
}
