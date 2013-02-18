package org.edgeframework.routing;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.vertx.java.deploy.impl.VertxLocator;

/**
 * Handles the chaining of request handlers
 * 
 * @author dteo
 */
class HandlerContext {
  private enum STATE {
    MIDDLEWARE,
    HANDLERS,
    COMPLETE,
    ERROR
  }

  private final HandlerContext that = this;

  private final List<RequestHandler> currentHandlers = new LinkedList<>();
  private final List<RequestHandler> middleware;

  private final Handlers handlers;

  private final HttpServerRequest request;
  private final HttpServerResponse response;

  private STATE state = STATE.MIDDLEWARE;

  public HandlerContext(final org.vertx.java.core.http.HttpServerRequest request, final List<RouteDefinition> routes, final List<RequestHandler> middleware) {
    this.handlers = new Handlers(request.method, request.path, routes);
    this.middleware = middleware;

    this.request = new HttpServerRequest(request);
    this.response = new HttpServerResponse(request.response);

    this.next();
  }

  // Passes control to the next handler in the chain
  // This includes middleware
  public void next() {
    RequestHandler handler = null;

    if (this.state == STATE.MIDDLEWARE) {
      handler = this.nextMiddleware();
      if (handler == null) {
        this.state = STATE.HANDLERS;
        handler = this.nextHandler();
      }
    } else if (this.state == STATE.HANDLERS) {
      handler = this.nextHandler();
    }

    // TODO: no more handlers
    System.out.println(this.request.getPath());
    System.out.println("IN: " + handler);
    handler._handle(this, this.request, this.response);
    System.out.println("OUT: " + handler);
  }

  // Jumps to the next route definition, abandoning the current one
  public void nextRoute() {
    if (this.state != STATE.HANDLERS) {
      this.currentHandlers.clear();
      this.next();
    }
  }

  public void exception(Exception t) {
    this.exception(t.toString());
  }

  public void exception(String message) {
    this.response.status(500);
    /* TODO: trigger exception handlers */

    VertxLocator.container.getLogger().error(message);

    this.response.send("<p>Server Error (500): " + message + "</p>");
  }

  private RequestHandler nextMiddleware() {
    // Go through the list of middleware
    // When all is completed, go on the handlers
    if (!this.middleware.isEmpty()) {
      return this.middleware.remove(0);
    } else {
      return null;
    }
  }

  private RequestHandler nextHandler() {
    // Get the next route in the chain if there is no route found as of yet
    // Then go through the list of handlers
    if (this.currentHandlers.isEmpty()) {
      RouteDefinitionMatchResult result = this.handlers.getNextMatch();
      if (result != null) {
        this.currentHandlers.addAll(Arrays.asList(result.route.getHandlers()));
        this.request.setParams(result.params);
      }
    }

    // TODO: end of routes
    return this.currentHandlers.remove(0);
  }
}
