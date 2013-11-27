package org.edgeframework.edge.core.groovy;

import org.edgeframework.edge.core.groovy._impl.DefaultApplication;
import org.vertx.groovy.platform.Verticle;
import org.vertx.java.core.Future;

public abstract class Edge extends Verticle {
  private DefaultApplication app;

  @Override
  public Object start(Future<Void> startedResult) {
    try {
      this.app = new DefaultApplication(this.getVertx());
      this.configure(this.app);

      this.app.start(startedResult);
    } catch (Throwable e) {
      e.printStackTrace();
      startedResult.setFailure(e);
    }

    return null;
  }

  @Override
  public Object stop() {
    this.app.stop();
    return null;
  }

  public void configure(Application app) {
  }
}