package org.edgeframework.eventbus.test;

import org.edgeframework.eventbus.EventBus;
import org.edgeframework.promises.PromiseHandler;
import org.junit.Test;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonObject;
import org.vertx.testtools.TestVerticle;

import static org.vertx.testtools.VertxAssert.*;

public class EventBusTestClient extends TestVerticle {
  private static final String REMOTE_NAMESPACE = "remote.namespace";

  @Override
  public void start() {
    super.start();
  }

  @Override
  public void stop() throws Exception {
    super.stop();
  }

  @Test
  public void testSend() {

    vertx.eventBus().registerHandler(REMOTE_NAMESPACE + ".testString", new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        assertEquals("Message sent on event bus is not correct", message.body.getString("message"), "Hello World");
        testComplete();
      }
    });

    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    server.testString("Hello World");
  }

  @Test
  public void testReceive() {
    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testString(String message) {
        assertTrue(message.equals("Hello World"));
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    vertx.eventBus().send(REMOTE_NAMESPACE + ".testString", new JsonObject().putString("message", "Hello World"));
  }

  @Test
  public void testBothEnds() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testString(String message) {
        assertTrue(message.equals("Hello World"));
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    server.testString("Hello World");
  }

  @Test
  public void testReply() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public String testReply(String message) {
        return message.toUpperCase();
      }
    }, EventBusTestsReceiver.class);

    server
        .testReply("Hello World")
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals(value, "HELLO WORLD", value);
            testComplete();

            return null;
          }
        });
  }

  @Test
  public void testMultipleParameters() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public String testMultipleParameters(String message, Integer value) {
        String result = "";

        while (value > 0) {
          result += message;
          value--;
        }

        return result;
      }
    }, EventBusTestsReceiver.class);

    server
        .testMultipleParameters("Hello World", 2)
        .then(new PromiseHandler<String, Void>() {

          @Override
          public Void handle(String value) {
            assertEquals("Multiple Parameters", "Hello WorldHello World", value);
            testComplete();

            return null;
          }
        });

  }

  @Test
  public void testParameterTypes1() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testParameterTypes1(byte n1, short n2, int n3, long n4) {
        assertTrue(n1 == 1);
        assertTrue(n2 == 2);
        assertTrue(n3 == 3);
        assertTrue(n4 == 4l);
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    server.testParameterTypes1((byte) 1, (short) 2, 3, 4l);
  }

  @Test
  public void testParameterTypes2() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testParameterTypes2(Byte n1, Short n2, Integer n3, Long n4) {
        assertTrue(n1 == 1);
        assertTrue(n2 == 2);
        assertTrue(n3 == 3);
        assertTrue(n4 == 4l);
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    server.testParameterTypes2((byte) 1, (short) 2, 3, 4l);
  }

  @Test
  public void testParameterTypes3() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testParameterTypes3(float f1, double f2) {
        assertTrue(f1 == 1.5f);
        assertTrue(f2 == 2.5);
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    server.testParameterTypes3(1.5f, 2.5);
  }

  @Test
  public void testParameterTypes4() {
    EventBusTestsSender server = EventBus.registerSender(vertx, REMOTE_NAMESPACE, EventBusTestsSender.class);

    EventBus.registerReceiver(vertx, REMOTE_NAMESPACE, new Receiver() {
      @Override
      public void testParameterTypes4(Float f1, Double f2) {
        assertTrue(f1 == 1.5f);
        assertTrue(f2 == 2.5);
        testComplete();
      }
    }, EventBusTestsReceiver.class);

    server.testParameterTypes4(1.5f, 2.5);
  }
}

class Receiver implements EventBusTestsReceiver {

  @Override
  public void testString(String message) {
  }

  @Override
  public String testReply(String message) {
    return null;
  }

  @Override
  public String testMultipleParameters(String message, Integer integer) {
    return null;
  }

  @Override
  public void testParameterTypes1(byte n1, short n2, int n3, long n4) {
  }

  @Override
  public void testParameterTypes2(Byte n1, Short n2, Integer n3, Long n4) {
  }

  @Override
  public void testParameterTypes3(float n1, double n2) {
  }

  @Override
  public void testParameterTypes4(Float n1, Double n2) {
  }
}