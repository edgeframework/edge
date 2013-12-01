package org.edgeframework.edge.core._internal;

import org.vertx.java.core.Future;
import org.vertx.java.core.http.HttpServerRequest;

public interface Application {
  void start(Future<Void> startedResult);

  void stop();

  void afterStart();

  void beforeStart();

  void beforeStop();

  void onError(Throwable e);

  void handle(HttpServerRequest request);
}