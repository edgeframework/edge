package org.edgeframework.promises;

public interface FailureHandler<O> extends CompletedHandler<Throwable> {
  public O handle(Throwable e);
}
