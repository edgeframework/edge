package org.edgeframework.edge.core.groovy

import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.Future

public abstract class Edge extends Verticle {
  private Application app

  public Application getApp() {
    return this.app
  }

  public Application app() {
    return this.app
  }

  @Override
  public def start(Future<Void> startedResult) {
    this.app = new Application(this.vertx)
    this.configure(this.app)

    this.app.start(startedResult)
  }

  @Override
  public def stop() {
    this.app.stop()
  }

  public def configure(Application app) {
  }
}