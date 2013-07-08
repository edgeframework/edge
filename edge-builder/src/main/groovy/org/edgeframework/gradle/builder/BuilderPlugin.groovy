package org.edgeframework.gradle.builder;

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.Sync;

class BuilderPlugin implements Plugin<Project>{

  @Override
  public void apply(Project target) {
    target.with {
      /* Plugins */
      // groovy plugin automatically applies java plugin
      // may need to pull in scala plugin in the future when supported
      apply plugin: 'groovy'

      /* Properties */
      convention.plugins.buildPlugin = new BuilderPluginConvention(it);

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
      File config = file("$configDir/config.gradle");
      println config
      if(config.exists()) {
        apply from: config
      }

      /* Add Tasks */
      task('copyToMods', type: Sync) {
        from sourceSets.main.output
        into vertxDir
      }

      /* */
      task('pullInDeps') {
        
      }

      println "HELLO!"
    }
  }

  class BuilderPluginConvention {
    private Project project;
    private String confDir = 'conf'

    BuilderPluginConvention(Project project) {
      this.project = project
    }

    void setConfigDir(String confDir) {
      this.confDir = confDir;
    }

    String getConfigDir(){
      return this.confDir;
    }

    File getVertxName() {
      return project.file("edge~${project.name}~${project.version}")
    }

    File getVertxDir() {
      return project.file("${project.rootDir}/mods/edge~${project.name}~${project.version}")
    }
  }
}