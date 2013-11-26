package org.edgeframework.edge.core._internal.http;

import org.vertx.java.core.http.HttpServerRequest;

public interface HttpRequestInternal {
  public void setRequest(HttpServerRequest request);
}
