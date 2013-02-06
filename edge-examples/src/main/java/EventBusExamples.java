import org.edgeframework.eventbus.EventBus;
import org.edgeframework.eventbus.EventBusParams;
import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.deploy.Verticle;

public class EventBusExamples extends Verticle {

  @Override
  public void start() throws Exception {

  }

  private void receive() {
    // Example 1
    vertx.eventBus().registerHandler("server.address.hello", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        message.reply("Hello " + message.body);
      }
    });

    // Example 2
    class Receiver {
      @EventBusParams("name")
      public String hello(String name) {
        return "Hello " + name;
      }
    }

    EventBus.registerReceiver("server.address", new Receiver(), Receiver.class);
  }

  private void sending() {
    // Example 1
    vertx.eventBus().send("server.address.hello", "Daryl", new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> reply) {
        System.out.println(reply.body);
      }
    });

    // Example 2
    SendingInterface sender = EventBus.registerSender("server.address", SendingInterface.class);
    sender.hello("Daryl")
        .then(new PromiseHandler<String, Void>() {
          @Override
          public Void handle(String value) {
            System.out.println(value);
            return null;
          }
        });
  }

  interface SendingInterface {
    @EventBusParams("name")
    public Promise<String> hello(String name);
  }

}
