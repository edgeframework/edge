package com.darylteo.edge.core.requests;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
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

  public EdgeHandlerContainer(final HttpServerRequest request, final RouteMatcher routeMatcher, final List<EdgeHandler> middleware) {
    this.routeMatcher = routeMatcher;
    this.middleware = middleware;

    this.request = new EdgeRequest(request);
    this.response = new EdgeResponse(request.response);

    if (this.request.isPost()) {
      request.bodyHandler(new Handler<Buffer>() {
        @Override
        public void handle(Buffer buffer) {
          that.request.setRawBody(buffer);
          main();
        }
      });
    } else {
      main();
    }

  }

  void next() {
    VertxLocator.vertx.runOnLoop(this.loopHandler);
  }

  public void exception() {
    this.exception("Internal Server Error");
  }

  public void exception(Throwable t) {
    this.exception(t.toString());
  }

  public void exception(String message) {
    this.response.status(500);
    /* TODO: trigger exception handlers */

    VertxLocator.container.getLogger().error(message);

    this.response.send("<p>Server Error (500): " + message + "</p>");
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
