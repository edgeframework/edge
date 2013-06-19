package org.edgeframework.gradle;

import org.gradle.api.Plugin
import org.gradle.api.Project

class Builder implements Plugin<Project>{
  @Override
  public void apply(Project target) {
    println target
  }
}