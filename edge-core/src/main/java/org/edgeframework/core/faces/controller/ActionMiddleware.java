package org.edgeframework.core.faces.controller;

import org.vertx.java.core.Handler;

import com.jetdrone.vertx.yoke.Middleware;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;

/**
 * Handles Yoke Request for the Action
 * 
 * @author dteo
 * 
 */
class ActionMiddleware extends Middleware {
  private final RequestAction action;

  public ActionMiddleware(RequestAction action) {
    this.action = action;
  }

  @Override
  public void handle(YokeRequest request, Handler<Object> next) {
    try {
      this.action.invoke(request);
    } catch (Throwable e) {
      e.printStackTrace();
      next.handle(e);
    }
  }

}
