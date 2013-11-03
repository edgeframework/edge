package org.edgeframework.edge.core.java;

import java.util.List;

import org.edgeframework.edge.core.api._internal.EdgeInternal;
import org.edgeframework.edge.core.api._internal.Engine;
import org.edgeframework.edge.core.api.filters.Filter;
import org.edgeframework.edge.core.java.http.Context;
import org.edgeframework.edge.core.java.http.Request;
import org.vertx.java.core.Future;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.platform.Verticle;

public class Edge extends Verticle implements org.edgeframework.edge.core.api.Edge<Vertx>, EdgeInternal {
  /* Engine */
  private Engine engine;

  /* Server */
  private HttpServer server;

  private int port;
  private String host;

  /* Filters and Delegates */
  
  
  /* Configuration Methods */
  public int port() {
    return this.port;
  }

  public Edge port(int port) {
    this.port = port;
    return this;
  }

  public String host() {
    return this.host;
  }

  public Edge host(String host) {
    this.host = host;
    return this;
  }

  /* Constructor */
  public Edge() {
    this.engine = new Engine();
  }

  @Override
  public void start(Future<Void> startedResult) {
    this.engine.start(this, startedResult);
  }

  @Override
  public void stop() {
    this.engine.stop(this);
  }

  /* Edge Interface Lifecycle Methods */
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
    error.printStackTrace();
  }

  /* EdgeInternal Interface Methods */
  @Override
  public void __defaults(int port, String host) {
    this.port = port;
    this.host = host;
  }

  @Override
  public final void __configure() {
    System.out.println("Configuring Edge");
    this.server = vertx.createHttpServer();
    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest vRequest) {
        Request request = new Request(vRequest);

        final Context context = new Context(Edge.this, request, Edge.this.filters());
        context.next();
      }
    });
  }

  @Override
  public final void __begin() {
    this.server.listen(this.port, this.host);
    System.out.println("Server Listening");
  }

  @Override
  public List<Filter> filters() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public Vertx vertx() {
    return this.vertx;
  }
}