package org.edgeframework.edge.core.api.controllers._internal;

public interface TypeConverterFunction<T1, T2> {
  public T2 call(T1 value);
}
