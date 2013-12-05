import org.edgeframework.edge.core.groovy.Application
import org.edgeframework.edge.core.groovy.delegates.AppDelegate

class RouteConfig implements AppDelegate{
  @Override
  public void afterStart(Application arg0) {
    println "Start"
  }

  @Override
  public void beforeStart(Application arg0) {
    println "Starting"
  }

  @Override
  public void beforeStop(Application arg0) {
    println "Stopping"
  }

  @Override
  public void onError(Application arg0, Throwable arg1) {
    println "Error"
    arg1.printStackTrace()
  }
}
