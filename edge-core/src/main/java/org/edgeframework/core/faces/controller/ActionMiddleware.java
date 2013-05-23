package org.edgeframework.core.faces.controller;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;

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
  
  public Middleware setVertx(Vertx vertx) {
    return super.setVertx(vertx);
  }

  @Override
  public void handle(YokeRequest request, Handler<Object> next) {
    try {
      this.action.invoke(vertx, request);
    } catch (Throwable e) {
      e.printStackTrace();
      next.handle(e);
    }
  }

}
