import java.io.File;
import java.nio.file.Paths;

import org.gradle.tooling.BuildLauncher;
import org.gradle.tooling.GradleConnector;
import org.gradle.tooling.ModelBuilder;
import org.gradle.tooling.ProjectConnection;
import org.gradle.tooling.model.GradleProject;

public class Daemon {
  public static void main(String args[]) {
    Daemon daemon = new Daemon();

    synchronized (daemon) {
      try {
        daemon.wait();
      } catch (InterruptedException e) {
      }
    }
  }

  public Daemon() {
    System.out.println("Running Daemon in: " + Paths.get("").toAbsolutePath());
    System.out.println("Creating Vertx Platform Manager");

    ProjectConnection conn = GradleConnector.newConnector().forProjectDirectory(new File("")).connect();

    conn
      .newBuild()
      .withArguments("--build-file", "conf/build.gradle")
      .setJvmArguments("-cp", "../bin")
      .forTasks("tasks")
      .run();

    conn.close();

    // PlatformManager man = PlatformLocator.factory.createPlatformManager();
    // new Module("main");
    //
    // /* Platform API vs shell exec? */
    // man.deployModule("vertx~daemon~main", new JsonObject(), 1, new
    // Handler<AsyncResult<String>>() {
    // @Override
    // public void handle(AsyncResult<String> event) {
    // if (event.succeeded()) {
    // System.out.println("Verticle Deployed: " + event.result());
    // } else {
    // System.out.println("Deployment Failed: " + event.cause());
    // }
    // }
    // });

  }
}