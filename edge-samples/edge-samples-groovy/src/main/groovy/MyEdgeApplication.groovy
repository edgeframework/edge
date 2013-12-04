import org.edgeframework.edge.core._internal.ApplicationEngine
import org.vertx.groovy.platform.Verticle

public class MyEdgeApplication extends Verticle {
  public MyEdgeApplication() {
    ApplicationEngine engine = new ApplicationEngine(vertx.toJavaVertx())
  }
}