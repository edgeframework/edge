package org.edgeframework.edge.core._lang_.app;

import org.edgeframework.edge.core._lang_.filters.mvc.Router;
import org.edgeframework.edge.core._lang_.http.Filter;

public interface Application {
  public void beforeStart(ApplicationEventHandler handler);

  public void afterStart(ApplicationEventHandler handler);

  public void beforeStop(ApplicationEventHandler handler);

  public void onError(ApplicationErrorEventHandler handler);

  public void onRequest(Filter filter);

  public Router getRouter();
}
