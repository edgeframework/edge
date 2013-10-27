package org.edgeframework.java.core.http;

public class Response {
  public Response setStatusCode(int i) {
    // TODO Auto-generated method stub
    return this;
  }

  public Response header(String key, String value) {
    // TODO Auto-generated method stub
    return this;
  }

  public Response header(String key, Object value) {
    return this.header(key, value.toString());
  }

  public Response write(String content) {
    // TODO Auto-generated method stub
    return this;
  }

  public Response close() {
    // TODO Auto-generated method stub
    return this;
  }

}
