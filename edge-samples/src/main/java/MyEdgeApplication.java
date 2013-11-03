import org.edgeframework.edge.core.java.Edge;

public class MyEdgeApplication extends Edge {
  @Override
  public void beforeStart() {
    System.out.println("Before Start");
  }

  @Override
  public void afterStart() {
    System.out.println("Server started");
  }
}
