package org.edgeframework.core.edges;

import org.edgeframework.core.exceptions.EdgeException;
import org.vertx.java.platform.Verticle;

import com.jetdrone.vertx.yoke.Yoke;

public abstract class AbstractEdge extends Verticle {
  private String name;

  private String host;
  private int port;
  private Yoke yoke;

  public AbstractEdge(String name, String host, int port) {
    this.name = name;
    this.host = host;
    this.port = port;
  }

  @Override
  public final void start() {
    /* Create YOKE */
    yoke = new Yoke(vertx);

    try {
      configure(yoke);
      beforeStart();
      yoke.listen(port, host);
      onStart();
    } catch (Exception e) {
      throw new EdgeException(e);
    }
  }

  /* Lifecycle */
  protected void configure(Yoke yoke) {
    // no-op
  }

  public void beforeStart() {
    // no-op
  }

  public void onStart() {
    // no-op
  }

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
