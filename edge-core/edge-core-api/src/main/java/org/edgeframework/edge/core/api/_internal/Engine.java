package org.edgeframework.edge.core.api._internal;

import org.edgeframework.edge.core.api.Edge;
import org.vertx.java.core.Future;

public class Engine {
  /* Verticle Methods */
  public <E extends Edge & EdgeInternal> void start(E edge, Future<Void> startedResult) {
    try {
      edge.beforeStart();
      edge.__configure();
      edge.__begin();
      edge.afterStart();

      startedResult.setResult(null);
    } catch (Throwable e) {
      edge.onError(e);
      startedResult.setFailure(e);
    }
  }

  public void stop(Edge edge) {
    try {
      edge.beforeStop();
    } catch (Throwable e) {
      edge.onError(e);
    }
  }
}
