package org.edgeframework.edge.core._internal.http;

public interface HttpResponseInternal {
  public HttpResponseInternal statusCode(int code);

  public HttpResponseInternal header(String key, Object value);

  public HttpResponseInternal header(String key, String value);

  public HttpResponseInternal write(String content);

  public HttpResponseInternal close();
}
