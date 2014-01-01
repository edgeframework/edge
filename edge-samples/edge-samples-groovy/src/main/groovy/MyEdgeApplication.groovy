import org.edgeframework.edge.core.groovy.app.Application
import org.edgeframework.edge.core.groovy.app.ApplicationEventHandler
import org.vertx.java.core.Future
import org.vertx.java.platform.Verticle

public class MyEdgeApplication extends Verticle {
  public void start(Future<Void> result) {
    Application app = new Application(vertx)

    System.out.println("Starting")

    app.afterStart({ println("It worked!") } as ApplicationEventHandler)

    app.getRouter().getRoutes().add("")

    app.start()
  }
}