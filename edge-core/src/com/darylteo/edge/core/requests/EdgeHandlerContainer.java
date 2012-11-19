package com.darylteo.edge.core.requests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeHandlerContainer {
  private final EdgeHandlerContainer that = this;

  private final List<EdgeHandler> currentHandlers = new LinkedList<>();
  private final List<EdgeHandler> middleware;

  private final RouteMatcher routeMatcher;

  private final EdgeRequest request;
  private final EdgeResponse response;

  /* RunLoop handler */
  private final Handler<Void> loopHandler = new Handler<Void>() {

    @Override
    public void handle(Void event) {
      VertxLocator.container.getLogger().info("Handling: " + request.getPath());

      /* No other handlers to process */
      if (that.currentHandlers.isEmpty()) {
        return;
      }

      /* Process the handler */
      EdgeHandler handler = currentHandlers.remove(0);
      handler.handle(that, request, response);
    }

  };

  public EdgeHandlerContainer(HttpServerRequest request, RouteMatcher routeMatcher, List<EdgeHandler> middleware) {
    this.routeMatcher = routeMatcher;
    this.middleware = middleware;

    this.request = new EdgeRequest(request);
    this.response = new EdgeResponse(request.response);

    main();
  }

  void next() {
    VertxLocator.vertx.runOnLoop(this.loopHandler);
  }

  private void main() {

    /* Add chain of Middleware to the beginning of the request chain */
    this.currentHandlers.addAll(this.middleware);

    final RouteMatcherResult routeResult = routeMatcher.getNextMatch();

    if (routeResult == null) {
      return;
    }

    this.currentHandlers.addAll(Arrays.asList(routeResult.route.getHandlers()));

    this.next();

  }
}
