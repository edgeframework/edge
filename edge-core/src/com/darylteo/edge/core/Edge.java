package com.darylteo.edge.core;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.Routing;

public class Edge {

  private final Edge that = this;
  private final Vertx vertx;

  private final HttpServer server;
  private final Routing routes;

  public static Edge newApplication() {
    return new Edge();
  }

  public static Edge newApplication(Vertx vertx) {
    return new Edge(vertx);
  }

  public Edge() {
    this(VertxLocator.vertx);
  }

  public Edge(Vertx vertx) {
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
  public Edge get(String urlPattern, Handler<EdgeRequest> handler) {
    this.routes.addRoute("GET", urlPattern, handler);
    return this;
  }

  public Edge post(String urlPattern, Handler<EdgeRequest> handler) {
    this.routes.addRoute("POST", urlPattern, handler);
    return this;
  }

  public Edge put(String urlPattern, Handler<EdgeRequest> handler) {
    this.routes.addRoute("PUT", urlPattern, handler);
    return this;
  }

  public Edge delete(String urlPattern, Handler<EdgeRequest> handler) {
    this.routes.addRoute("DELETE", urlPattern, handler);
    return this;
  }

  public Edge listen(int port) {
    return this.listen(port, "localhost");
  }

  public Edge listen(int port, String hostname) {
    this.server.listen(port, hostname);
    return this;
  }

  /* Server Wrapper */
  private void handleRequest(HttpServerRequest request) {
    VertxLocator.container.getLogger().info("Request Received: " + request);

    RouteMatcher matcher = this.routes.getRouteMatcher(request.method, request.path);
    EdgeRequest requestWrapper = new EdgeRequest(request, matcher);
  }
}
