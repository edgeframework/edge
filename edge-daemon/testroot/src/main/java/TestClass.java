import org.vertx.java.platform.Verticle;

public class TestClass extends Verticle {
  @Override
  public void start() {
    System.out.println("Starting!1");
  }

  @Override
  public void stop() {
    System.out.println("Stopping!");
  }
}
