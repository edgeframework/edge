import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.Edge
import org.edgeframework.edge.core.groovy.filters.Filter

@CompileStatic
public class MyEdgeApplication extends Edge {
  @Override
  public def configure(Application app) {
    app.delegates.add(
      beforeStart: {
        app.filters.add({ ctx ->
          println 'Request!'
          ctx.response.write('Blah!!!!').close()
        } as Filter)
      },
      beforeStop: { println "$it:stop" },
      afterStart: { println "$it:start" },
      onError: { println "$app:error" }
      )
  }
}
