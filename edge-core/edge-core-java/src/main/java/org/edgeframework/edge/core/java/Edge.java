package org.edgeframework.edge.core.java;

import org.edgeframework.edge.core.java._internal.DefaultApplication;
import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class Edge extends Verticle {
  private DefaultApplication app;

  public Application getApp() {
    return this.app;
  }

  public Application app() {
    return this.app;
  }

  public Edge() {
  }

  @Override
  public void start(Future<Void> startedResult) {
    try {
      this.app = new DefaultApplication(this.vertx);
      this.configure(this.app);

      this.app.start(startedResult);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }

  @Override
  public void stop() {
    this.app.stop();
  }

  public void configure(Application app) {
  }
}