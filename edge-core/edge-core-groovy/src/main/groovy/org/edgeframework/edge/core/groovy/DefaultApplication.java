package org.edgeframework.edge.core.groovy;

import org.edgeframework.edge.core.api._DefaultApplication;
import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer;
import org.vertx.groovy.core.Vertx;

public class DefaultApplication extends _DefaultApplication<AppDelegateContainer> implements Application {
  public DefaultApplication(Vertx vertx) {
    super(vertx.toJavaVertx(), new AppDelegateContainer());
  }
}
