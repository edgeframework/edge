package org.edgeframework.core.faces;

import org.vertx.java.core.http.HttpServer;
import org.vertx.java.platform.Verticle;

import com.jetdrone.vertx.yoke.Yoke;

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
    /* Create YOKE */
    Yoke yoke = new Yoke(vertx);
    configure(yoke);
    yoke.listen(port, host);
  }

  public abstract void configure(Yoke yoke);

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
