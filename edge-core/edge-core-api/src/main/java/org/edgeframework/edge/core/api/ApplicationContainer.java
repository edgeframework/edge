package org.edgeframework.edge.core.api;

/**
 * Indicates a class that contains an instance of Application, and allows configuration.
 * @author Daryl Teo
 *
 * @param <A> the Application class contained within
 */
public interface ApplicationContainer<A extends Application> {
  public A app();

  public A getApp();

  public void configure(A app);
}
