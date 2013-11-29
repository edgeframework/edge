import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.Edge

@CompileStatic
public class MyEdgeApplication extends Edge {
  //  @Override
  //  public def configure(Application app) {
  //    app.delegates.add(onError : { Application _, Throwable err ->
  //      println 'An error has occurred!'
  //      err.printStackTrace()
  //    })
  //    app.delegates.add(new RouteConfig())
  //  }
  @Override
  public def configure(Application app) {
    //    app.delegates.add(onError : { Application _, Throwable err ->
    //      println 'An error has occurred!'
    //      err.printStackTrace()
    //    })
    //    app.delegates.add(new RouteConfig())

    app.delegates.add()
  }
}