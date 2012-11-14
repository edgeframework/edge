package com.darylteo.edge.core.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeHandlerContainer {
  private final EdgeHandlerContainer that = this;

  private final HttpServerRequest _request;

  private final List<EdgeHandler> currentHandlers = new LinkedList<>();

  private final RouteMatcher routeMatcher;
  private RouteMatcherResult currentResult;

  private final Map<String, Object> params = new HashMap<>();

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.routeMatcher = routeMatcher;

    this._request = request;

    this.next();
    this.handle();
  }

  void next() {
    this.currentHandlers.clear();
    this.params.clear();

    this.currentResult = routeMatcher.getNextMatch();

    if (this.currentResult == null) {
      return;
    }

    this.currentHandlers.addAll(Arrays.asList(this.currentResult.route.getHandlers()));
  }

  private void handle() {
    final EdgeRequest request = new EdgeRequest(_request, params);
    final EdgeResponse response = new EdgeResponse(_request.response);

    final Handler<Void> loopHandler = new Handler<Void>() {

      @Override
      public void handle(Void event) {
        VertxLocator.container.getLogger().info("Handling: " + request.getPath());

        if (currentHandlers.isEmpty()) {
          return;
        }

        EdgeHandler handler = currentHandlers.remove(0);
        handler.handle(that, request, response);

        VertxLocator.vertx.runOnLoop(this);
      }

    };

    VertxLocator.vertx.runOnLoop(loopHandler);
  }
}
