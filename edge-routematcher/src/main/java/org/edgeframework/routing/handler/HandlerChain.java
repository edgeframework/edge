package org.edgeframework.routing.handler;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.routing.RouteMatcher;
import org.edgeframework.routing.RouteMatcherResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

public class HandlerChain {
  private final HandlerChain that = this;

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

  public HandlerChain(final HttpServerRequest request, final RouteMatcher routeMatcher, final List<EdgeHandler> middleware) {
    this.routeMatcher = routeMatcher;
    this.middleware = middleware;

    this.request = new EdgeRequest(request);
    this.response = new EdgeResponse(request.response);

    processPostBody(this.request)
        .then(new PromiseHandler<Void, Void>() {
          @Override
          public Void handle(Void value) {
            main();
            return null;
          }
        });

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
    final RouteMatcherResult routeResult = routeMatcher.getNextMatch();

    if (routeResult == null) {
      return;
    }

    /* Add chain of Middleware to the beginning of the request chain */
    this.currentHandlers.addAll(this.middleware);
    this.currentHandlers.addAll(Arrays.asList(routeResult.route.getHandlers()));

    this.next();
  }

  private Promise<Void> processPostBody(final EdgeRequest request) {
    final Promise<Void> promise = Promise.defer();

    if (request.isPost()) {
      request.getUnderlyingRequest().bodyHandler(new Handler<Buffer>() {
        @Override
        public void handle(Buffer buffer) {
          request.setRawBody(buffer);
          promise.fulfill(null);
        }
      });
    } else {
      promise.fulfill(null);
    }

    return promise;
  }
}
