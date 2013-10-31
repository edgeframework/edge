package org.edgeframework.core.java;

import java.util.LinkedList;
import java.util.List;

import org.edgeframework.core.api.http.Context;
import org.edgeframework.core.api.http.Request;
import org.edgeframework.core.filters.Filter;
import org.vertx.java.core.Handler;
import org.vertx.java.core.http.HttpServer;
import org.vertx.java.core.http.HttpServerRequest;

public abstract class Edge extends org.edgeframework.core.api.Edge {

  /* Private Lifecycle Hooks */
  public void __configure() throws Exception {
    HttpServer server = vertx.createHttpServer();
    server.requestHandler(new Handler<HttpServerRequest>() {
      @Override
      public void handle(HttpServerRequest vRequest) {
        final Request request = new Request(vRequest);
        final Context context = new Context(Edge.this, request);

        context.begin();
      }
    });
  }

  public void __begin() throws Exception {

  }
}
