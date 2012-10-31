package com.darylteo.edge.core.requests;

import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;

public class EdgeRequest {
  private final HttpServerRequest request;

  private Map<String, String> params;

  public final String method;
  public final String uri;
  public final String path;
  public final String query;

  public EdgeRequest(HttpServerRequest request) {
    this.request = request;

    this.method = request.method;
    this.uri = request.uri;
    this.path = request.path;
    this.query = request.query;
  }

  /**
   * Retrieve a URL Param
   */
  public Object param(String identifier) {
    return this.params.get(identifier);
  }

  /**
   * Retrieves a Http Response Header
   * 
   * @param header
   * @return
   */
  public String header(String header) {
    return this.request.headers().get(header);
  }

  /**
   * Used to update the params based on the route match
   * 
   * @param params
   */
  void setParams(Map<String, String> params) {
    this.params = params;
  }

}
