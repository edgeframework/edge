package org.edgeframework.edge.core._lang_.filters.mvc;

import java.util.HashMap;
import java.util.Map;

public class MatchResult {
  private Map<String, Object> params = new HashMap<>();

  public boolean matches() {
    return this.params != null;
  }

  public Map<String, Object> getParams() {
    return this.params;
  }

  void setParam(String key, Object value) {
    this.params.put(key, value);
  }
}
