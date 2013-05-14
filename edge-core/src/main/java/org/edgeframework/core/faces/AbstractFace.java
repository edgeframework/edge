package org.edgeframework.core.faces;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.platform.Verticle;

public abstract class AbstractFace extends Verticle {
  private String name;

  private String host;
  private int port;
  private HttpServer server;

  public AbstractFace(String name, String host, int port) {
    this.name = name;
    this.host = host;
    this.port = port;
  }

  @Override
  public void start() {
    /* Create web server */
    server = vertx.createHttpServer();
    container.logger().info("Server Configuration");
    configureServer(server);
    server.listen(port, host);
  }

  abstract void configureServer(HttpServer server);

  /* Accessors */
  public String getName() {
    return name;
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }
}
