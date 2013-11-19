package org.edgeframework.edge.core.groovy

import groovy.transform.CompileStatic

import org.edgeframework.edge.core.groovy.delegates.AppDelegateContainer
import org.vertx.groovy.core.Vertx

@CompileStatic
public class Application {
  private org.edgeframework.edge.core.java._internal.DefaultApplication jApplication

  public int getPort() {
    return this.jApplication.port
  }

  public int port(){
    return this.jApplication.port
  }

  public Application setPort(int port) {
    this.jApplication.port(port)
    return this
  }

  public Application port(int port) {
    this.jApplication.port(port)
    return this
  }

  public String getHost() {
    return this.jApplication.host
  }

  public String host() {
    return this.jApplication.host
  }

  public Application setHost(String host) {
    this.jApplication.host(host)
    return this
  }

  public Application host(String host)  {
    this.jApplication.host(host)
    return this
  }

  private AppDelegateContainer delegates
  public AppDelegateContainer delegates() {
    return this.delegates
  }

  public AppDelegateContainer getDelegates() {
    return this.delegates
  }

  public Application(Vertx vertx) {
    this.jApplication = new org.edgeframework.edge.core.java._internal.DefaultApplication(vertx.toJavaVertx())
    this.delegates = new AppDelegateContainer(this.jApplication.delegates)
  }
}
