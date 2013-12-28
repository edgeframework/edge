package org.edgeframework.edge.core.java.app;

import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public abstract class Edge extends Verticle {
  public void start(Future<Void> result) {
    Application app = new Application(vertx);
    this.configure(app);

    app.start();
  }

  public abstract void configure(Application app);
}