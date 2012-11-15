package com.darylteo.edge.core.requests;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
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

  private final HttpServerRequest request;

  private final List<EdgeHandler> currentHandlers = new LinkedList<>();

  private final RouteMatcher routeMatcher;
  private RouteMatcherResult currentResult;

  private final Map<String, Object> params = new HashMap<>();

  private final List<EdgeHandler> middleware;

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher, List<EdgeHandler> middleware) {
    this.routeMatcher = routeMatcher;

    this.request = request;

    this.middleware = middleware;

    main();
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

  private void main() {
    final EdgeRequest request = new EdgeRequest(this.request, params);
    final EdgeResponse response = new EdgeResponse(this.request.response);

    middleware(request, response);
  }

  private void middleware(final EdgeRequest request, final EdgeResponse response) {

    final Iterator<EdgeHandler> iterator = this.middleware.iterator();

    final Handler<Void> loopHandler = new Handler<Void>() {

      @Override
      public void handle(Void event) {
        VertxLocator.container.getLogger().info("Handling: " + request.getPath());

        if (!iterator.hasNext()) {
          that.next();
          that.handle(request, response);
          return;
        }

        EdgeHandler middleware = iterator.next();
        middleware.handle(that, request, response);

        VertxLocator.vertx.runOnLoop(this);
      }

    };

    VertxLocator.vertx.runOnLoop(loopHandler);
  }

  private void handle(final EdgeRequest request, final EdgeResponse response) {

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
