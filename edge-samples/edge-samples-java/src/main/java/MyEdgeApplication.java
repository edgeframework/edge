import org.edgeframework.edge.core.java.app.Application;
import org.edgeframework.edge.core.java.app.ApplicationEventHandler;
import org.edgeframework.edge.core.java.app.DefaultApplication;
import org.vertx.java.core.Future;
import org.vertx.java.platform.Verticle;

public class MyEdgeApplication extends Verticle {
  public void start(Future<Void> result) {
    DefaultApplication app = new DefaultApplication(vertx);

    System.out.println("Starting");

    app.afterStart(new ApplicationEventHandler() {
      @Override
      public void call(Application app) {
        System.out.println("It worked!");
      }
    });

    app.getRouter().getRoutes().add("");

    app.start();
  }
}