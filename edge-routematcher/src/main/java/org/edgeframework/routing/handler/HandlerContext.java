package org.edgeframework.routing.handler;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.routing.HttpServerRequest;
import org.edgeframework.routing.HttpServerResponse;
import org.edgeframework.routing.RouteDefinitionMatchResult;
import org.edgeframework.routing.Routes;
import org.vertx.java.deploy.impl.VertxLocator;

/**
 * Handles the chaining of request handlers
 * 
 * @author dteo
 */
public class HandlerContext {
  private enum STATE {
    MIDDLEWARE_SETUP,
    MIDDLEWARE,
    PARAMS_SETUP,
    PARAMS,
    HANDLERS_SETUP,
    HANDLERS,
    COMPLETE,
    ERROR,
  }

  private final HandlerContext that = this;

  private List<Handler> handlers = new LinkedList<>();
  private RouteDefinitionMatchResult currentRoute = null;

  private final Handlers requestHandlers;

  private final Routes routes;

  private final HttpServerRequest request;
  private final HttpServerResponse response;

  private STATE state = STATE.MIDDLEWARE_SETUP;

  public HandlerContext(final org.vertx.java.core.http.HttpServerRequest request, Routes routes) {
    this.requestHandlers = new Handlers(request.method, request.path, routes.getRouteDefinitions());

    this.routes = routes;

    this.request = new HttpServerRequest(request);
    this.response = new HttpServerResponse(request.response);

    this.next();
  }

  // Passes control to the next handler in the chain
  // This includes middleware
  public void next() {

    while (true) {
      Handler handler = null;

      switch (this.state) {
      case MIDDLEWARE_SETUP:

        this.handlers.addAll(this.routes.getMiddleware());
        this.state = STATE.MIDDLEWARE;

        continue;

      case MIDDLEWARE:

        if (this.handlers.isEmpty()) {
          this.state = STATE.PARAMS_SETUP;
          continue;
        }

        this.handlers.remove(0)._begin(this, this.request, this.response);

        break;

      case PARAMS_SETUP:

        this.currentRoute = this.requestHandlers.getNextMatch();

        if (this.currentRoute == null) {
          this.state = STATE.COMPLETE;
          continue;
        }

        this.state = STATE.PARAMS;
        this.request.setParams(this.currentRoute.getParams());

        for (String name : currentRoute.getParams().keySet()) {
          this.handlers.addAll(this.routes.getHandlersForParam(name));
        }

        continue;

      case PARAMS:

        if (this.handlers.isEmpty()) {
          this.state = STATE.HANDLERS_SETUP;
          continue;
        }

        this.handlers.remove(0)._begin(this, this.request, this.response);

        break;

      case HANDLERS_SETUP:

        this.handlers.addAll(this.currentRoute.getRoute().getHandlers());
        this.state = STATE.HANDLERS;

        continue;

      case HANDLERS:

        if (this.handlers.isEmpty()) {
          this.state = STATE.PARAMS;
          continue;
        }

        this.handlers.remove(0)._begin(this, this.request, this.response);

        break;

      default:
        break;
      }

      break;
    }

  }

  public void exception(Exception e) {
    e.printStackTrace();
    this.exception(e.toString());
  }

  public void exception(String message) {
    this.response.status(500);
    /* TODO: trigger exception handlers */

    VertxLocator.container.getLogger().error(message);

    this.response.send("<p>Server Error (500): " + message + "</p>");
  }
}
