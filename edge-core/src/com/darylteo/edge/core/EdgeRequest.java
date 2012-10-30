package com.darylteo.edge.core;

import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.http.HttpServerResponse;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeRequest {
  private final HttpServerRequest request;
  private final HttpServerResponse response;

  private final RouteMatcher routeMatcher;

  private boolean shouldStop = false;
  private Map<String, String> params;

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

    this.response.close();
  }

  /**
   * Handlers call this to indicate that the request should not pass through to
   * other handlers
   **/
  public EdgeRequest stop() {
    this.shouldStop = true;
    return this;
  }

  /**
   * Renders a String to the response
   */
  public EdgeRequest renderText(String text) {
    return this.renderText(text, 200);
  }

  /**
   * Renders a String to the response
   */
  public EdgeRequest renderText(String text, int status) {
    this.response.end(text);
    return this;
  }

  /**
   * Renders a String to the response
   */
  public EdgeRequest renderTemplate(String templateName) {
    return this.renderTemplate(templateName, 200);
  }

  public EdgeRequest renderTemplate(String templateName, int status) {
    this.response.end("Rendered: " + templateName);
    return this;
  }

  /**
   * Retrieve a URL Param
   */
  public String param(String identifier) {
    return this.params.get(identifier);
  }

  /**
   * This sets the Http Response status value
   * 
   * @param value
   * @return
   */
  public EdgeRequest status(int value) {
    this.response.statusCode = value;
    return this;
  }

  /**
   * Retrieves a Http Response Header
   * 
   * @param header
   * @return
   */
  public Object header(String header) {
    return this.response.headers().get(header);
  }

  /**
   * Sets a Http Response Header
   * 
   * @param header
   * @param value
   * @return
   */
  public EdgeRequest header(String header, Object value) {
    this.response.headers().put(header, value);
    return this;
  }

  /* Private Methods */
  private boolean handle() {
    boolean handled = false;

    while (true) {
      RouteMatcherResult result = routeMatcher.getNextMatch();

      if (result == null) {
        return handled;
      }

      VertxLocator.container.getLogger().info("Route Match for " + this.uri);

      /* ShouldStop is used to determine if this will be the last handler */
      this.shouldStop = false;
      this.params = result.params;

      result.route.getHandler().handle(this);
      handled = true;

      if (this.shouldStop) {
        return handled;
      }
    }
  }

}
