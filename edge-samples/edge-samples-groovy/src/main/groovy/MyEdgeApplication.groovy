import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.app.Application
import org.edgeframework.edge.core.groovy.app.DefaultApplication
import org.vertx.groovy.platform.Verticle

@CompileStatic
public class MyEdgeApplication extends Verticle {
  public def start() {
    Application app = new DefaultApplication(vertx.toJavaVertx())

    app.router.routes.add("")
  }
}