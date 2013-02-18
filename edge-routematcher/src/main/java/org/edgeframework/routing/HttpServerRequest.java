package org.edgeframework.routing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.edgeframework.promises.Promise;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.buffer.Buffer;

public class HttpServerRequest {
  private final org.vertx.java.core.http.HttpServerRequest request;

  private Map<String, Object> params;
  private Map<String, Object> query;
  private Map<String, Object> body;
  private Map<String, Object> data;

  private Map<String, String> files;

  private Buffer bodyBuffer;

  public HttpServerRequest(org.vertx.java.core.http.HttpServerRequest request) {
    this.request = request;

    this.params = new HashMap<>();
    this.body = new HashMap<>();
    this.data = new HashMap<>();
    this.query = QueryParser.parse(request.query);

    this.files = new HashMap<>();
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

  @SuppressWarnings("unchecked")
  public Map<String, Object> getBody() {
    return this.data.containsKey("body") ? (Map<String, Object>) this.data.get("body") : null;
  }

  public Map<String, Object> getData() {
    return this.data;
  }

  void setParams(Map<String, Object> params) {
    this.params = new HashMap<>(params);
  }

  void setBody(Map<String, Object> body) {
    this.body = new HashMap<>(body);
  }

  public Promise<byte[]> getRawBody() {
    // Return a promise which is fulfilled by the handlers
    final Promise<byte[]> promise = Promise.defer();

    // check if the body has already been loaded
    if (this.bodyBuffer != null) {
      promise.fulfill(this.bodyBuffer.getBytes());
      return promise;
    }

    // Implementation Note:
    // Do NOT call endHandler after bodyHandler as that
    // overrides the handler.
    // bodyHandler returns you to entire body
    this.request.bodyHandler(new Handler<Buffer>() {
      @Override
      public void handle(Buffer data) {
        HttpServerRequest.this.bodyBuffer = data;
        promise.fulfill(data.getBytes());
      }
    });

    return promise;
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
