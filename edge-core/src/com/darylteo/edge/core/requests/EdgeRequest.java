package com.darylteo.edge.core.requests;

import java.util.Map;

import org.vertx.java.core.http.HttpServerRequest;

import com.darylteo.edge.core.routing.RouteMatcherResult;

public class EdgeRequest {
  private final HttpServerRequest request;

  private RouteMatcherResult routeMatcherResult;
  private Map<String, Object> query;

  public EdgeRequest(HttpServerRequest request, RouteMatcherResult result) {
    this.request = request;
    this.routeMatcherResult = result;

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
   * Retrieves a value given the parameter name.
   * 
   * @param name
   * @return
   */
  public String getRouteParam(String name) {
    return this.routeMatcherResult.params.get(name);
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
   * Returns the Query component of this request
   * 
   * @return
   */
  public String getQuery() {
    return request.query;
  }

}
