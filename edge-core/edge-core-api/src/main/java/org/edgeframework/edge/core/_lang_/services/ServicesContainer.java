package org.edgeframework.edge.core._lang_.services;

import java.util.LinkedList;
import java.util.List;

public class ServicesContainer {
  private List<Object> services = new LinkedList<>();

  public void add(Object service) {
    this.services.add(service.getClass());
  }

  public <T> T get(Class<T> type) {
    for (Object service : services) {
      if (type.isAssignableFrom(service.getClass())) {
        return (T) service;
      }
    }

    return null;
  }
}
