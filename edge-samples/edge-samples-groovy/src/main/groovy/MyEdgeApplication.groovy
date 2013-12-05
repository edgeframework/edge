import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.ApplicationEngine
import org.vertx.groovy.platform.Verticle

@CompileStatic
public class MyEdgeApplication extends Verticle {
  @Override
  public Object start() {
    ApplicationEngine engine = new ApplicationEngine(vertx.toJavaVertx())
  }
}