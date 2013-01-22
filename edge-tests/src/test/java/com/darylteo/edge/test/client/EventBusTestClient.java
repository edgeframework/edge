package com.darylteo.edge.test.client;

import org.edgeframework.eventbus.EventBus;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.framework.TestClientBase;

import com.darylteo.edge.test.util.TestEventBusReceiver;
import com.darylteo.edge.test.util.TestEventBusSender;

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

    TestEventBusSender server = EventBus.createProxy(TestEventBusSender.class);

    server.testString("Hello World");
  }

  public void testReceive() {
    EventBus.registerReceiver(new TestEventBusReceiver() {
      @Override
      public String testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();

        return null;
      }
    });

    VertxLocator.vertx.eventBus().send("testString", "Hello World");
  }

  public void testBothEnds() {
    TestEventBusSender server = EventBus.createProxy(TestEventBusSender.class);

    EventBus.registerReceiver(new TestEventBusReceiver() {
      @Override
      public String testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();

        return null;
      }
    });

    server.testString("Hello World");
  }
}
