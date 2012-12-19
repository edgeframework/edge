package org.edgeframework.app;

import org.edgeframework.routing.Routing;
import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.middleware.Assets;
import org.edgeframework.routing.middleware.BodyParser;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.deploy.impl.VertxLocator;

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
    this.routes = new Routing();
    this.server.requestHandler(this.routes);
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

  public static EdgeHandler bodyParser() {
    return EdgeApplication.bodyParser;
  }

  public static EdgeHandler assets(String path) {
    return new Assets(path);
  }

  public EdgeApplication use(EdgeHandler... handlers) {
    for (EdgeHandler handler : handlers) {
      this.routes.addMiddleware(handler);
    }
    return this;
  }

}
