package org.edgeframework.edge.core.api.http;

import java.util.Map;

public interface Request {
  public Map<String, String> params();

  public Response response();

  public String path();
}
