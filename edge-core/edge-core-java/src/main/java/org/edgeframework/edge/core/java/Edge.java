package org.edgeframework.edge.core.java;

import org.vertx.java.core.Future;
import org.vertx.java.core.Vertx;
import org.vertx.java.platform.Verticle;

public class Edge extends Verticle implements org.edgeframework.edge.core.api.Edge<Vertx, Application> {
  private final Application app;

  public Application getApp() {
    return this.app;
  }

  public Application app() {
    return this.app;
  }

  public Edge() {
    this.app = new Application(this);
    this.configure(this.app);
  }

  public Vertx vertx() {
    return this.vertx;
  }

  @Override
  public void start(Future<Void> startedResult) {
    this.app.start(startedResult);
  }

  @Override
  public void stop() {
    this.app.stop();
  }

  @Override
  public void configure(Application app) {
  }
}