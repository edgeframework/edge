import org.edgeframework.edge.core.java.Application;
import org.edgeframework.edge.core.java.Edge;
import org.edgeframework.edge.core.java.delegates.Delegate;

public class MyEdgeApplication extends Edge {

  @Override
  public void configure(Application app) {
    app.delegates().add(new Delegate() {
      @Override
      public void beforeStart(Application app) {
        System.out.println("Application Starting");
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
