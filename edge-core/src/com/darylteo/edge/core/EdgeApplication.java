package com.darylteo.edge.core;

import java.util.LinkedList;
import java.util.List;

import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.deploy.impl.VertxLocator;

import com.darylteo.edge.core.requests.EdgeHandler;
import com.darylteo.edge.core.requests.EdgeHandlerContainer;
import com.darylteo.edge.core.routing.RouteMatcher;
import com.darylteo.edge.core.routing.Routing;
import com.darylteo.edge.middleware.Assets;
import com.darylteo.edge.middleware.BodyParser;

public class EdgeApplication {

  private final EdgeApplication that = this;
  private final Vertx vertx;

  private final HttpServer server;
  private final Routing routes;

  public final List<EdgeHandler> middleware;

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
    this.middleware = new LinkedList<>();
  }

  /* Server Functions */
  public EdgeApplication get(String urlPattern, EdgeHandler... handlers) {
    this.routes.addRoute("GET", urlPattern, handlers);
    return this;
  }

  public EdgeApplication post(String urlPattern, EdgeHandler... handlers) {
    this.routes.addRoute("POST", urlPattern, handlers);
    return this;
  }

  public EdgeApplication put(String urlPattern, EdgeHandler... handlers) {
    this.routes.addRoute("PUT", urlPattern, handlers);
    return this;
  }

  public EdgeApplication delete(String urlPattern, EdgeHandler... handlers) {
    this.routes.addRoute("DELETE", urlPattern, handlers);
    return this;
  }

  public EdgeApplication all(String urlPattern, EdgeHandler... handlers) {
    return this.get(urlPattern, handlers)
        .post(urlPattern, handlers)
        .put(urlPattern, handlers)
        .delete(urlPattern, handlers);
  }

  public EdgeApplication listen(int port) {
    return this.listen(port, "localhost");
  }

  public EdgeApplication listen(int port, String hostname) {
    this.server.listen(port, hostname);
    return this;
  }

  /* Middleware */
  public static final EdgeHandler bodyParser = new BodyParser();
  
  public static EdgeHandler bodyParser(){
    return EdgeApplication.bodyParser;
  }
  
  public static EdgeHandler assets(String path){
    return new Assets(path);
  }
  
  public EdgeApplication use(EdgeHandler... handlers) {
    for (EdgeHandler handler : handlers) {
      this.middleware.add(handler);
    }
    return this;
  }

  /* Server Wrapper */
  private void handleRequest(HttpServerRequest request) {
    RouteMatcher matcher = this.routes.getRouteMatcher(request.method, request.path);
    new EdgeHandlerContainer(request, matcher, this.middleware);
  }
}
