package org.edgeframework.edge.core.groovy

import org.edgeframework.edge.core.api._internal.EdgeInternal
import org.edgeframework.edge.core.api._internal.Engine
import org.edgeframework.edge.core.api.filters.Filter
import org.edgeframework.edge.core.groovy.http.Context
import org.edgeframework.edge.core.groovy.http.Request
import org.vertx.groovy.core.Vertx
import org.vertx.groovy.core.http.HttpServer
import org.vertx.groovy.core.http.HttpServerRequest
import org.vertx.groovy.platform.Verticle
import org.vertx.java.core.Future

public class Edge extends Verticle implements org.edgeframework.edge.core.api.Edge<Vertx>, EdgeInternal{
  private Engine engine

  private HttpServer server

  int port
  String host

  public Edge() {
    this.engine = new Engine()
  }

  @Override
  public def start(Future<Void> startedResult) {
    this.engine.start(this, startedResult)
  }

  @Override
  public def stop() {
    this.engine.stop(this)
  }

  @Override
  public void beforeStart() {
  }

  @Override
  public void afterStart() {
  }

  @Override
  public void beforeStop() {
  }

  @Override
  public void onError(Throwable error) {
    error.printStackTrace()
  }

  /* Private Lifecycle Hooks */
  @Override
  public void __defaults(int port, String host) {
    this.port = port
    this.host = host
  }

  @Override
  public void __configure() {
    server = vertx.createHttpServer()
    server.requestHandler { HttpServerRequest vRequest ->
      Request request = new Request(vRequest)
      Context context = new Context(vertx, request, [])
      context.next()
    }
  }

  public void __begin() {
    this.server.listen(this.port, this.host)
  }

  @Override
  public List<Filter> filters() {
    // TODO Auto-generated method stub
    return null
  }

  @Override
  public Vertx vertx() {
    return this.vertx
  }
}