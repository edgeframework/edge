package org.edgeframework.core.util.org.edgeframework.routing.test;

import org.edgeframework.promises.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;

public class TestHttpClient {

  private static final Logger logger = LoggerFactory.getLogger(TestHttpClient.class);
  private HttpClient client;

  public TestHttpClient(Vertx vertx, String hostname, int port) {
    this.client = vertx.createHttpClient()
        .setHost(hostname)
        .setPort(port);
  }

  public Promise<String> getPage(String url) {
    DataHandler handler = new DataHandler();
    get(url, handler);

    return handler.promise;
  }

  public Promise<String> postPage(String url, String data) {
    DataHandler handler = new DataHandler();
    post(url, data, handler);

    return handler.promise;
  }

  public Promise<Integer> getPageStatus(String url) {
    StatusHandler handler = new StatusHandler();
    get(url, handler);

    return handler.promise;
  }

  public Promise<Integer> postPageStatus(String url, String data) {
    StatusHandler handler = new StatusHandler();
    post(url, data, handler);

    return handler.promise;
  }

  private void get(String url, Handler<HttpClientResponse> handler) {
    HttpClientRequest request = this.client.get(url, handler);
    request.end();
  }

  private void post(String url, String data, Handler<HttpClientResponse> handler) {
    HttpClientRequest request = this.client.post(url, handler);

    request.headers().put("content-type", "application/x-www-form-urlencoded");
    request.headers().put("content-length", data.length());
    request.write(data);
    request.end();
  }

  public void close() {
    this.client.close();
    this.client = null;
  }

  private class DataHandler implements Handler<HttpClientResponse> {

    public Promise<String> promise = Promise.defer();

    @Override
    public void handle(HttpClientResponse response) {

      response.bodyHandler(new Handler<Buffer>() {

        @Override
        public void handle(Buffer buffer) {
          logger.debug(buffer.toString());
          promise.fulfill(buffer.toString());
        }

      });
    }

  }

  private class StatusHandler implements Handler<HttpClientResponse> {

    public Promise<Integer> promise = Promise.defer();

    @Override
    public void handle(HttpClientResponse response) {
      promise.fulfill(response.statusCode);
    }

  }
}
