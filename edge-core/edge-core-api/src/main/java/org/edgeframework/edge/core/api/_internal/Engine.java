package org.edgeframework.edge.core.api._internal;

import org.edgeframework.edge.core.api.Edge;
import org.vertx.java.core.Future;

public class Engine {
  /* Verticle Methods */
  public <E extends Edge<?> & EdgeInternal> void start(E edge, Future<Void> startedResult) {
    try {
      /* Set Defaults */
      edge.port(8000);
      edge.host("localhost");

      /* Lifecycle */
      edge.beforeStart();
      edge.__configure();
      edge.__begin();

      /* Begin */
      startedResult.setResult(null);
      edge.afterStart();
    } catch (Throwable e) {
      edge.onError(e);
      startedResult.setFailure(e);
    }
  }

  public void stop(Edge<?> edge) {
    try {
      edge.beforeStop();
    } catch (Throwable e) {
      edge.onError(e);
    }
  }
}
