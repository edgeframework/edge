package com.darylteo.edge.test.client;

import org.edgeframework.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.framework.TestClientBase;

import com.darylteo.edge.test.util.TestEventBusInterface;

public class EventBusTestClient extends TestClientBase {
  private Vertx vertx = VertxLocator.vertx;

  @Override
  public void start() {
    super.start();

    tu.appReady();
  }

  @Override
  public void stop() {
    super.stop();
  }

  public void testSend() {

    VertxLocator.vertx.eventBus().registerHandler("testString", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        tu.azzert(message.body.equals("Hello World"), "Message sent on event bus is not correct");
        tu.testComplete();
      }
    });

    TestEventBusInterface server = EventBus.createProxy(TestEventBusInterface.class);

    server.testString("Hello World");
  }

  public void testReceive() {
    EventBus.registerReceiver(new TestEventBusInterface() {
      @Override
      public void testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();
      }
    });

    VertxLocator.vertx.eventBus().send("testString", "Hello World");
  }

  public void testBothEnds() {
    TestEventBusInterface server = EventBus.createProxy(TestEventBusInterface.class);

    EventBus.registerReceiver(new TestEventBusInterface() {
      @Override
      public void testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();
      }
    });

    server.testString("Hello World");
  }
}
