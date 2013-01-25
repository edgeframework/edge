package com.darylteo.edge.test.client;

import org.edgeframework.eventbus.EventBus;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.Vertx;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.impl.VertxLocator;
import org.vertx.java.testframework.TestClientBase;

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

    VertxLocator.vertx.eventBus().registerHandler("testString", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        tu.azzert(message.body.getString("message").equals("Hello World"), "Message sent on event bus is not correct");
        tu.testComplete();
      }
    });

    TestEventBusSender server = EventBus.createProxy(TestEventBusSender.class);

    server.testString("Hello World");
  }

  public void testReceive() {
    EventBus.registerReceiver(new TestEventBusReceiver() {
      @Override
      public void testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();
      }

      @Override
      public String testReply(String message) {
        return null;
      }
    });

    VertxLocator.vertx.eventBus().send("testString", new JsonObject().putString("message", "Hello World"));
  }

  public void testBothEnds() {
    TestEventBusSender server = EventBus.createProxy(TestEventBusSender.class);

    EventBus.registerReceiver(new TestEventBusReceiver() {
      @Override
      public void testString(String message) {
        tu.azzert(message.equals("Hello World"));
        tu.testComplete();
      }

      @Override
      public String testReply(String message) {
        // TODO Auto-generated method stub
        return null;
      }
    });

    server.testString("Hello World");
  }

  public void testReply() {
    TestEventBusSender server = EventBus.createProxy(TestEventBusSender.class);

    EventBus.registerReceiver(new TestEventBusReceiver() {
      @Override
      public void testString(String message) {
      }

      @Override
      public String testReply(String message) {
        return message.toUpperCase();
      }
    });

    server
        .testReply("Hello World")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            tu.azzert(value.equals("HELLO WORLD"), "Did not reply message properly");
            tu.testComplete();

            return null;
          }
        });

  }
}
