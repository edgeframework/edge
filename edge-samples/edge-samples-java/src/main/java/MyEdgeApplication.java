import org.edgeframework.edge.core.java.Application;
import org.edgeframework.edge.core.java.Edge;
import org.edgeframework.edge.core.java.delegates.AppDelegate;
import org.edgeframework.edge.core.java.filters.Filter;
import org.edgeframework.edge.core.java.http.HttpContext;

public class MyEdgeApplication extends Edge {
  @Override
  public void configure(Application app) {
    app.delegates().add(new AppDelegate() {
      @Override
      public void beforeStart(Application app) {
        System.out.println("Application Starting");

        app.filters().add(new Filter() {
          @Override
          public void action(HttpContext context) {
            System.out.println(context.request().path());
            context.response().write("Blah!").close();
          }
        });
      }

      @Override
      public void beforeStop(Application app) {
        System.out.println("Application Stopping");
      }

      @Override
      public void afterStart(Application app) {
        System.out.println("Application Started");
      }

      @Override
      public void onError(Throwable e) {
        System.out.println("Error Occurred!");
        e.printStackTrace();
      }
    });
  }
}
