package org.edgeframework.edge.core._lang_.services;

import java.util.HashMap;
import java.util.Map;

public class ServicesContainer {
  private Map<Object, Object> services = new HashMap<>();

  public void add(String key, Object service) {
    this.services.put(key, service);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(String key, Class<T> type) {
    return (T) services.get(key);
  }

  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> type) {
    for (Object service : services.values()) {
      if (type.isAssignableFrom(service.getClass())) {
        return (T) service;
      }
    }

    return null;
  }
}
