package org.edgeframework.edge.core.java;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.platform.Verticle;

public class Application extends org.edgeframework.edge.core.api._internal.BaseApplication<Verticle, Application> {
  public Application(Verticle verticle) {
    super(verticle);
  }

  @Override
  protected void startServer(Verticle verticle, int port, String host) {
    HttpServer server = verticle.getVertx().createHttpServer();

    server.listen(port, host);
  }
}
