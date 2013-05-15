package org.edgeframework.core.faces;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

import org.vertx.java.core.Handler;

import com.jetdrone.vertx.yoke.Yoke;
import com.jetdrone.vertx.yoke.middleware.Router;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;

public abstract class ControllerFace extends AbstractFace {
  private String routesPath = "";
  private Router router = new Router();

  public ControllerFace(String name, String host, int port, String routesPath) {
    super(name, host, port);

    this.routesPath = routesPath;
  }

  @Override
  void configure(Yoke yoke) {
    yoke.use(router);
  }

  protected void register(final String method, final String pattern, final Class<? extends Controller> controller, final String action) throws Exception {
    final MethodHandle handle = MethodHandles.publicLookup().findVirtual(controller, action, MethodType.methodType(Controller.Result.class));

    Handler<YokeRequest> handler = new Handler<YokeRequest>() {
      @Override
      public void handle(YokeRequest request) {
        try {
          Controller receiver = controller.newInstance();

          /* Setup Receiver */
          receiver.setRequest(request);

          /* Invoke Receiver, receive result */
          Controller.Result result = (Controller.Result) handle.invoke(receiver);

          /* Render result to response */
          result.render(request.response());
        } catch (Throwable e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    };

    System.out.println(controller);
    router.all(pattern, handler);
  }

  public String getRoutesPath() {
    return routesPath;
  }

  protected abstract class RouteDefinition {
    private String method;
    private Class<?> controller;
    private String action;
  }
}
