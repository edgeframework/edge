package org.edgeframework.gradle.builder;

import org.gradle.api.Plugin
import org.gradle.api.Project

class BuilderPlugin implements Plugin<Project>{
  @Override
  public void apply(Project target) {
    println target
  }
}