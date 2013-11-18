import org.edgeframework.edge.core.java.Application
import org.edgeframework.edge.core.java.Edge
import org.edgeframework.edge.core.java.delegates.AppDelegate
import org.edgeframework.edge.core.java.filters.Filter
import org.edgeframework.edge.core.java.http.HttpContext

public class MyEdgeApplication extends Edge {
  @Override
  public void configure(Application app) {
    app.delegates.add([
      beforeStart: {
        app.filters.add({ ctx ->
          println 'Request!'
          ctx.response.write('Blah!').close()
        } as Filter)
      },
      beforeStop: { println it },
      afterStart: { println it },
      onError: { println it }
    ] as AppDelegate)
  }
}
