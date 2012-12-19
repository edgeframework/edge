import org.edgeframework.app.EdgeApplication;
import org.edgeframework.routing.handler.EdgeHandler;
import org.edgeframework.routing.handler.EdgeRequest;
import org.edgeframework.routing.handler.EdgeResponse;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;
import org.vertx.java.deploy.Verticle;

public class BasicExample extends Verticle {

  private static final String HOSTNAME = "localhost";
  private static final int PORT = 8080;

  @Override
  public void start() throws Exception {
    EdgeApplication edge = new EdgeApplication();

    edge

        /* Index */
        .get("/", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putArray("links", new JsonArray()
                    .addObject(new JsonObject()
                        .putString("name", "Basic Example")
                        .putString("url", "/examples/basic")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "Basic Example (Multiple Handlers)")
                        .putString("url", "/examples/multiple")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "POST Example")
                        .putString("url", "/examples/post")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "Server Error (500)")
                        .putString("url", "/examples/exception")
                    )
                    .addObject(new JsonObject()
                        .putString("name", "File Not Found (404)")
                        .putString("url", "/examples/random")
                    )
                );

            response.render("index", context);
          }

        })

        .get("/examples/basic", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.render("basic");
          }
        })

        .get("/examples/multiple", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            request.getData().put("pass", "through");
            next();
          }
        }, new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putString("pass", (String) request.getData().get("pass"));

            response.render("basic", context);
          }
        })

        .get("/examples/post", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            response.render("post");
          }
        })
        .post("/examples/post", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            JsonObject context = new JsonObject()
                .putObject("body", new JsonObject(request.getBody()));

            response.render("post", context);
          }
        })

        .get("/examples/exception", new EdgeHandler() {

          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) {
            Object obj = null;
            obj.toString();
          }

        })

        .all("*", new EdgeHandler() {
          @Override
          public void handleRequest(EdgeRequest request, EdgeResponse response) throws Exception {
            /* 404 */
            response
                .status(404)
                .render("404");
          }
        })

        .use(EdgeApplication.assets("public"))
        .use(EdgeApplication.bodyParser())

        .listen(PORT, HOSTNAME);
  }

}
