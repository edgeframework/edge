package org.edgeframework.routing;

import java.util.HashMap;
import java.util.Map;

import org.vertx.java.core.buffer.Buffer;

public class HttpServerRequest {
  private final org.vertx.java.core.http.HttpServerRequest request;

  private Map<String, Object> params;
  private Map<String, Object> query;
  private Map<String, Object> body;
  private Map<String, byte[]> files;
  private Map<String, Object> data;

  private Buffer bodyBuffer;
  private byte[] bodyBytes;

  public HttpServerRequest(org.vertx.java.core.http.HttpServerRequest request) {
    this.request = request;

    this.params = new HashMap<>();
    this.data = new HashMap<>();
    this.body = new HashMap<>();
    this.files = new HashMap<>();

    this.query = QueryParser.parse(request.query);
  }

  public byte[] getPostBody() {
    if (this.bodyBytes == null) {
      this.bodyBytes = this.bodyBuffer.getBytes();
    }

    return this.bodyBytes;
  }

  public void setPostBody(Buffer buffer) {
    this.bodyBuffer = buffer;
  }

  public org.vertx.java.core.http.HttpServerRequest getUnderlyingRequest() {
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
  public Map<String, Object> getQuery() {
    return this.query;
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

  public void setParams(Map<String, Object> params) {
    this.params = new HashMap<>(params);
  }

  /**
   * Retrieves a map of request body attribute data and their associated values.
   * 
   * @param name
   * @return
   */
  public Map<String, Object> getBody() {
    return this.body;
  }

  /**
   * Retrieves a map of uploaded files contents (as byte[]), mapped by their
   * form names.
   * 
   * @param name
   * @return
   */
  public Map<String, byte[]> getFiles() {
    return this.files;
  }

  /**
   * Retrieves a map of data parameters and their associated values. These are
   * preserved between a handler chain for a route.
   * 
   * @param name
   * @return
   */
  public Map<String, Object> getData() {
    return this.data;
  }

  public boolean isPost() {
    return this.request.method.equalsIgnoreCase("post");
  }

  public boolean isGet() {
    return this.request.method.equalsIgnoreCase("get");
  }

  public boolean isPut() {
    return this.request.method.equalsIgnoreCase("put");
  }

  public boolean isDelete() {
    return this.request.method.equalsIgnoreCase("delete");
  }

}
