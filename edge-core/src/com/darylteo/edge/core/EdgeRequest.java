package com.darylteo.edge.core;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.Route;
import com.darylteo.edge.core.routing.RouteMatcher;

public class EdgeRequest {
  private final HttpServerRequest request;
  private final HttpServerResponse response;

  private final RouteMatcher routeMatcher;

  private boolean shouldStop = false;

  public final String method;
  public final String uri;
  public final String path;
  public final String query;

  public EdgeRequest(HttpServerRequest request, RouteMatcher routeMatcher) {
    this.request = request;
    this.response = request.response;

    this.routeMatcher = routeMatcher;

    this.method = request.method;
    this.uri = request.uri;
    this.path = request.path;
    this.query = request.query;

    this.handle();
  }

  /**
   * Handlers call this to indicate that the request should not pass through to
   * other handlers
   **/
  public void stop() {
    this.shouldStop = true;
  }

  /**
   * Renders a String to the response
   */
  public void renderText(String text) {
    this.renderText(text, 200);
  }

  /**
   * Renders a String to the response
   */
  public void renderText(String text, int status) {
    this.response.end(text);
  }

  /**
   * Renders a String to the response
   */
  public void renderTemplate(String templateName) {
    this.renderTemplate(templateName, 200);
  }

  public void renderTemplate(String templateName, int status) {
    this.response.end("Rendered: " + templateName);
  }

  private boolean handle() {
    boolean handled = false;

    while (true) {
      Route route = routeMatcher.getNextMatch();

      VertxLocator.container.getLogger().info("Route Match for " + this.uri + ":" + route);

      if (route == null) {
        return handled;
      }

      /* ShouldStop is used to determine if this will be the last handler */
      this.shouldStop = false;
      route.getHandler().handle(this);
      handled = true;

      if (this.shouldStop) {
        return handled;
      }
    }
  }

}
