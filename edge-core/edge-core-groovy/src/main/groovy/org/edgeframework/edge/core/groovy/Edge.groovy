package org.edgeframework.edge.core.groovy

import org.vertx.groovy.core.Vertx
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.Future

public abstract class Edge extends Verticle implements org.edgeframework.edge.core.api.Edge<Vertx, Application> {
  final Application app

  public Application app() {
    return this.app
  }

  public Edge() {
    this.app = new Application(this.vertx)
    this.configure(this.app)
  }

  @Override
  public def start(Future<Void> startedResult) {
    this.app.start(startedResult)
  }

  @Override
  public def stop() {
    this.app.stop()
  }
}