package org.edgeframework.edge.core.groovy;

import org.edgeframework.edge.core.api._ApplicationEngine;
import org.vertx.groovy.platform.Verticle;
import org.vertx.java.core.Future;

public class Edge extends Verticle {

  @Override
  public Object start(Future<Void> startedResult) {
    startedResult.setResult(null);

    DefaultApplication app = new DefaultApplication(this.getVertx());
    _ApplicationEngine<Application> engine = new _ApplicationEngine<Application>(app);
    return null;
  }
}
