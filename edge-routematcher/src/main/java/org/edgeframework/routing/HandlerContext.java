package org.edgeframework.routing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.edgeframework.promises.FailureHandler;
import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.deploy.impl.VertxLocator;

/**
 * Handles the chaining of request handlers
 * 
 * @author dteo
 */
class HandlerContext {
  private final HandlerContext that = this;

  private final List<RequestHandler> currentHandlers = new LinkedList<>();
  private final List<RequestHandler> middleware;

  private final Handlers handlers;

  private final HttpServerRequest request;
  private final HttpServerResponse response;

  // The Loop Handler
  SimpleHandler loopHandler = new SimpleHandler() {
    @Override
    protected void handle() {
      /* No other handlers to process */
      if (that.currentHandlers.isEmpty()) {
        return;
      }

      /* Process the handler */
      RequestHandler handler = that.currentHandlers.remove(0);
      handler._handle(that, request, response);
    }
  };

  public HandlerContext(final org.vertx.java.core.http.HttpServerRequest request, final List<RouteDefinition> routes, final List<RequestHandler> middleware) {
    this.handlers = new Handlers(request.method, request.path, routes);
    this.middleware = middleware;

    this.request = new HttpServerRequest(request);
    this.response = new HttpServerResponse(request.response);

    processPostBody(this.request)
        .then(new PromiseHandler<Void, Void>() {
          @Override
          public Void handle(Void value) {
            main();
            return null;
          }
        }, new FailureHandler<Void>() {
          @Override
          public Void handle(Exception e) {
            exception(e);
            return null;
          }
        });

  }

  public void next() {
    VertxLocator.vertx.runOnLoop(loopHandler);
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
    final MatcherResult routeResult = handlers.getNextMatch();

    if (routeResult == null) {
      return;
    }

    /* Add chain of Middleware to the beginning of the request chain */
    this.currentHandlers.addAll(this.middleware);
    this.currentHandlers.addAll(Arrays.asList(routeResult.route.getHandlers()));
    this.request.setParams(routeResult.params);

    this.next();
  }

  /* TODO: Why is this here? Shouldn't this be in the middleware? */
  private Promise<Void> processPostBody(final HttpServerRequest request) {
    final Promise<Void> promise = Promise.defer();

    if (request.isPost()) {
      request.getUnderlyingRequest().bodyHandler(new Handler<Buffer>() {
        @Override
        public void handle(Buffer buffer) {
          request.setPostBody(buffer);
          promise.fulfill(null);
        }
      });
    } else {
      promise.fulfill(null);
    }

    return promise;
  }

}
