package org.edgeframework.core.tests.mains;

import java.net.URLClassLoader;

import org.edgeframework.core.tests.faces.AdminControllerFace;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.Handler;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.platform.PlatformLocator;
import org.vertx.java.platform.PlatformManager;

public class RoutedControllerMain {
  public static void main(String args[]) throws InterruptedException {
    new RoutedControllerMain();
  }

  public RoutedControllerMain() throws InterruptedException {
    PlatformManager manager = PlatformLocator.factory.createPlatformManager();
    manager.deployVerticle(
      AdminControllerFace.class.getCanonicalName(),
      new JsonObject(),
      ((URLClassLoader) (Thread.currentThread().getContextClassLoader())).getURLs(),
      1,
      null,
      new Handler<AsyncResult<String>>() {
        @Override
        public void handle(AsyncResult<String> event) {
          if (event.succeeded()) {
            System.out.println("Server Up: " + event.result());
          } else {
            event.cause().printStackTrace();
            System.out.println("Server down: " + event.cause());
          }
        }
      }
      );

    synchronized (this) {
      this.wait();
    }
  }
}
