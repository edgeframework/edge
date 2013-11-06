package org.edgeframework.edge.core.api.delegates;

import java.util.List;

/**
 * Indicates a class that contains a list of Application Delegates
 * 
 * @author Daryl Teo
 * 
 */
public interface DelegatesContainer extends Iterable<Delegate> {
  public List<Delegate> getDelegates();

  public List<Delegate> delegates();
}
