package org.edgeframework.java.core.controller;

public interface TypeConverterFunction<T1, T2> {
  public T2 call(T1 value);
}
