package com.darylteo.edge.core.requests;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;
import org.vertx.java.core.json.JsonObject;

public class EdgeRequest {
  private final HttpServerRequest request;

  public JsonObject params;
  public JsonObject query;

  public final String method;
  public final String uri;
  public final String path;

  public EdgeRequest(HttpServerRequest request) {
    this.request = request;

    this.method = request.method;
    this.uri = request.uri;
    this.path = request.path;
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
    this.params = new JsonObject(new HashMap<String, Object>(params));
  }

  /**
   * Used to update the params based on the route match
   * 
   * @param params
   */
  void setQuery(Map<String, String> params) {
    this.query = new JsonObject(new HashMap<String, Object>(params));
  }

}
