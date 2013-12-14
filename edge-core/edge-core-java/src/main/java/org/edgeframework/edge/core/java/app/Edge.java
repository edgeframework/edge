package org.edgeframework.edge.core.java.app;

import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class Edge extends Verticle {
  public void start(Future<Void> result) {
    Application app = new Application(vertx);
    app.start();
  }
}