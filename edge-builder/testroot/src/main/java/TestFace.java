import org.edgeframework.core.faces.impl.StaticFace;
import org.vertx.java.platform.Verticle;
import com.google.common.base.Optional;

public class TestFace extends StaticFace {
  /*
   * If this code compiles, this means the following tests are fulfilled: 
   *  - Edge-core is pulled into the classpath appropriately
   *  - Yoke is pulled into the classpath appropriately.
   *  - vertx-platform is pulled in appropriately.
   *  - unmanaged libs are pulled in appropriately.
   */
  public TestFace() {
    super("Static Assets", "localhost", 8080, "assets");
    
    /* Test unmanaged libs */
    Optional<Integer> possible = Optional.of(5);
    possible.isPresent(); // returns true
    possible.get(); // returns 5
    
    /* Test Verticle */
    Verticle verticle = this;
  }
}