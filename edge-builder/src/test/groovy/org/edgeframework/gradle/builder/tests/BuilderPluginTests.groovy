package org.edgeframework.gradle.builder.tests;

import static org.junit.Assert.*;

import org.edgeframework.gradle.builder.BuilderPlugin;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

class BuilderPluginTests {
  @Test
  public void test() {
    Project project = ProjectBuilder.builder().withProjectDir(new File("testroot")).withName("root").build()
    project.apply plugin: BuilderPlugin
  }
}
