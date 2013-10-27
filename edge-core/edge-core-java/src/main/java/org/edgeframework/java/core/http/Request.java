package org.edgeframework.java.core.http;

import org.vertx.java.core.MultiMap;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class Request {
  private Response response;

  public Request() {
    this.response = new Response();
  }

  public MultiMap getParams() {
    // TODO Auto-generated method stub
    throw new NotImplementedException();
  }

  public Response getResponse() {
    // TODO Auto-generated method stub
    return response;
  }

}
