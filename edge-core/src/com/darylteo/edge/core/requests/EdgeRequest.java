package com.darylteo.edge.core.requests;

import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;

public class EdgeRequest {
  private final HttpServerRequest request;

  private ParamCollection params;
  private QueryCollection query;
  private DataCollection data;

  public EdgeRequest(HttpServerRequest request) {
    this.request = request;

    this.params = new ParamCollection();
    this.query = QueryParser.parse(request.query);

    this.data = new DataCollection();
  }

  public HttpServerRequest getUnderlyingRequest() {
    return this.request;
  }

  /**
   * Retrieves a Http Response Header
   * 
   * @param header
   * @return
   */
  public String getHeader(String header) {
    return this.request.headers().get(header);
  }

  /**
   * Retrieves all Http Response Headers
   * 
   * @param header
   * @return
   */
  public Map<String, String> getHeaders() {
    return this.request.headers();
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
  public QueryCollection getQuery() {
    return this.query;
  }

  /**
   * Retrieves a map of route parameters and their associated values
   * 
   * @param name
   * @return
   */
  public ParamCollection getParams() {
    return this.params;
  }

  /**
   * Retrieves a map of data parameters and their associated values. These are
   * preserved between a handler chain for a route.
   * 
   * @param name
   * @return
   */
  public DataCollection getData() {
    return this.data;
  }

}
