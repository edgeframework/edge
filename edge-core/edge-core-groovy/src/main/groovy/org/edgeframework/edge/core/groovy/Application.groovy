package org.edgeframework.edge.core.groovy

import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer

public class Application extends org.edgeframework.edge.core.api._internal.BaseApplication<Vertx, Application, HttpServer> {
  public Application(Vertx vertx) {
    super(vertx)
  }

  @Override
  protected HttpServer startServer(int port, String host) {
    HttpServer server = this.verticle().createHttpServer()

    server.listen(port, host)

    return server
  }
}
