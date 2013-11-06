package org.edgeframework.edge.core.java.http;

import org.vertx.java.core.Vertx;

public interface HttpContext {
  public Vertx getVertx();

  public Vertx vertx();

  public HttpRequest getRequest();

  public HttpRequest request();

  public HttpResponse getResponse();

  public HttpResponse response();
}
