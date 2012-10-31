package com.darylteo.edge.core.requests;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;

public class EdgeRequest {
  private final HttpServerRequest request;

  public Map<String, String> params;
  public Map<String, Object> query;

  public final String method;
  public final String uri;
  public final String path;

  public EdgeRequest(HttpServerRequest request) {
    this.request = request;

    this.method = request.method;
    this.uri = request.uri;
    this.path = request.path;

    this.query = QueryParser.parse(request.query);
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
    this.params = new HashMap<String, String>(params);
  }

}
