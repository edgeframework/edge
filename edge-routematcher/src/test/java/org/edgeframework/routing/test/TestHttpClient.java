package org.edgeframework.routing.test;


import org.edgeframework.promises.Promise;
import org.vertx.java.core.Handler;
import org.vertx.java.core.SimpleHandler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.http.HttpClient;
import org.vertx.java.core.http.HttpClientRequest;
import org.vertx.java.core.http.HttpClientResponse;
import org.vertx.java.deploy.impl.VertxLocator;

public class TestHttpClient {

  private HttpClient client;

  public TestHttpClient(String hostname, int port) {
    this.client = VertxLocator.vertx.createHttpClient()
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

      final Buffer data = new Buffer();

      response.dataHandler(new Handler<Buffer>() {

        @Override
        public void handle(Buffer buffer) {
          data.appendBuffer(buffer);
        }

      });

      response.endHandler(new SimpleHandler() {

        @Override
        protected void handle() {
          promise.fulfill(data.toString());
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
