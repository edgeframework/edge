package org.edgeframework.core.api.controllers;

public interface TypeConverterFunction<T1, T2> {
  public T2 call(T1 value);
}
