package com.darylteo.edge.core;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeHandlerContainer;
import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.Routing;

public class EdgeApplication {

  private final EdgeApplication that = this;
  private final Vertx vertx;

  private final HttpServer server;
  private final Routing routes;

  public EdgeApplication() {
    this(VertxLocator.vertx);
  }

  public EdgeApplication(Vertx vertx) {
    this.vertx = vertx;

    this.server = vertx.createHttpServer();
    this.server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest request) {
        that.handleRequest(request);
      }
    });

    this.routes = new Routing();
  }

  /* Server Functions */
  public EdgeApplication get(String urlPattern, EdgeHandler handler) {
    this.routes.addRoute("GET", urlPattern, handler);
    return this;
  }

  public EdgeApplication post(String urlPattern, EdgeHandler handler) {
    this.routes.addRoute("POST", urlPattern, handler);
    return this;
  }

  public EdgeApplication put(String urlPattern, EdgeHandler handler) {
    this.routes.addRoute("PUT", urlPattern, handler);
    return this;
  }

  public EdgeApplication delete(String urlPattern, EdgeHandler handler) {
    this.routes.addRoute("DELETE", urlPattern, handler);
    return this;
  }

  public EdgeApplication listen(int port) {
    return this.listen(port, "localhost");
  }

  public EdgeApplication listen(int port, String hostname) {
    this.server.listen(port, hostname);
    return this;
  }

  /* Server Wrapper */
  private void handleRequest(HttpServerRequest request) {
    RouteMatcher matcher = this.routes.getRouteMatcher(request.method, request.path);
    EdgeHandlerContainer requestWrapper = new EdgeHandlerContainer(request, matcher);

    requestWrapper.handle();
  }
}
