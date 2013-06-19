package org.edgeframework.gradle.builder;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync;

class BuilderPlugin implements Plugin<Project>{
  @Override
  public void apply(Project target) {
    target.with {
      /* Plugins */
      apply plugin: 'groovy'

      /* Properties */
      buildDir = '.build'
      ext.confDir = 'conf'

      /* Add Dependencies */
      repositories {
        if(!hasProperty('release') || !release) {
          mavenLocal()
        }

        mavenCentral()
      }

      configurations {
        edge
        unmanaged

        compile {
          extendsFrom edge
          extendsFrom unmanaged
        }
      }

      dependencies {
        edge "org.edgeframework:edge-core:1.0.0-ALPHA1-SNAPSHOT"
        edge "io.vertx:vertx-platform:2.0.0-CR1"

        unmanaged fileTree('libs'){ include '*.jar' }
      }
      
      /* Allow custom configuration */
      apply from: file("$confDir/build.gradle")
    }
  }
}