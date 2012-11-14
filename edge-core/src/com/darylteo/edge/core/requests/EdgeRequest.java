package com.darylteo.edge.core.requests;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;

public class EdgeRequest {
  private final HttpServerRequest request;

  private Map<String, Object> params;
  private Map<String, Object> query;

  public EdgeRequest(HttpServerRequest request, Map<String, Object> params) {
    this.request = request;
    this.params = new HashMap<>(params);

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
   * Retrieves a map of route parameters and their associated values
   * 
   * @param name
   * @return
   */
  public Map<String, Object> getParams() {
    return this.params;
  }

  /**
   * Returns the method of this request.
   * 
   * @return
   */
  public String getMethod() {
    return request.method;
  }

  /**
   * Returns the full uri of this request. For example,
   * "http://domain.org/index.html?query=1"
   * 
   * @return
   */
  public String getUri() {
    return request.uri;
  }

  /**
   * Returns the path component of this request. For example, "/index.html"
   * 
   * @return
   */
  public String getPath() {
    return request.path;
  }

  /**
   * Returns the Query of this request
   * 
   * @return
   */
  public Map<String, Object> getQuery() {
    return this.query;
  }

}
