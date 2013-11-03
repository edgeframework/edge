package org.edgeframework.edge.core.api.delegates;

import java.util.List;

import org.edgeframework.edge.core.api.Application;

/**
 * Indicates a class that contains a list of Application Delegates
 * 
 * @author Daryl Teo
 * 
 */
public interface DelegatesContainer<A extends Application> {
  public List<Delegate<A>> getDelegates();

  public List<Delegate<A>> delegates();
}
