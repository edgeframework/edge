package org.edgeframework.edge.core.api.http;

public interface Response {
  public Response statusCode(int code);

  public Response header(String key, Object value);

  public Response header(String key, String value);

  public Response write(String content);

  public Response close();
}
