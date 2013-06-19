package org.edgeframework.gradle.builder.tests;

import static org.junit.Assert.*;

import org.edgeframework.gradle.builder.BuilderPlugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

class BuilderPluginTests {
  @Test
  public void testClasspath() {
    Project project = ProjectBuilder.builder().withProjectDir(new File("testroot")).withName("root").build()
    project.apply plugin: BuilderPlugin

    project.evaluate()
    executeTask(project, "clean")
    executeTask(project, "build")

    project.with {
      assertTrue("Build was not successful: possibly a classpath issue", file("$buildDir/libs/root.jar").isFile());
    }
  }

  def executeTask(Project project, String taskName) {
    Task task = project.tasks.findByPath(taskName)
    Set executed = [] as Set

    return task ? executeTask(task,executed) : null
  }

  def executeTask(Task task, Set executed) {
    for (t in task.taskDependencies.getDependencies(task)) {
      if(t in executed) {
        continue
      }

      executeTask(t,executed)
    }

    task.execute()
    executed.add(task)
  }
}
