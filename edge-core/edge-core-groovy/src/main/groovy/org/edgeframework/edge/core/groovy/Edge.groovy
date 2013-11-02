package org.edgeframework.edge.core.groovy

import org.edgeframework.edge.core.api._internal.Engine
import org.edgeframework.edge.core.groovy.http.Context
import org.edgeframework.edge.core.groovy.http.Request
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.Future

public abstract class Edge extends Verticle {
  private Engine engine

  private HttpServer server

  private int port
  private String host

  public Edge() {
    this.engine = new Engine()
  }

  @Override
  public Object start(Future<Void> startedResult) {
    this.engine.start(this, startedResult)
  }

  @Override
  public Object stop() {
    this.engine.stop(this)
  }

  /* Private Lifecycle Hooks */
  public final void __configure() throws Exception {
    server = vertx.createHttpServer()
    server.requestHandler { HttpServerRequest vRequest ->
      Request request = new Request(vRequest)
      Context context = new Context(vertx, request, [])
    }
  }

  public final void __begin() throws Exception {
    this.server.listen(this.port, this.host)
  }
}