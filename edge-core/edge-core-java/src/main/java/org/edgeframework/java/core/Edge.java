package org.edgeframework.java.core;

import org.edgeframework.java.core.routing.Router;
import org.vertx.java.platform.Verticle;

public abstract class Edge extends Verticle {
  /* ^.*\.(?<action>\w*\(.*\))$ */
  private Router router = new Router();

  /* Verticle Methods */
  @Override
  public void start(org.vertx.java.core.Future<Void> startedResult) {
    try {
      beforeStart();
      _configure();
      _begin();
      afterStart();
    } catch (Throwable e) {
      onError(e);
    }
  }

  @Override
  public void stop() {
    try {
      beforeStop();
    } catch (Throwable e) {
      onError(e);
    }
  }

  /* Lifecycle Hooks */
  public void beforeStart() {
  }

  public void afterStart() {
  }

  public void beforeStop() {
  }

  public void onError(Throwable error) {
  }

  public void registerRoutes(Router router) {
  }

  /* Private Lifecycle Hooks */
  private void _configure() throws Exception {
  }

  private void _begin() throws Exception {
  }
}
