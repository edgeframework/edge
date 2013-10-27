package org.edgeframework.java.core.exceptions;

public class EdgeException extends RuntimeException {
  private static final long serialVersionUID = 1L;

  public EdgeException(Throwable cause) {
    super(cause);
  }

  public EdgeException(String reason) {
    super(reason);
  }

  public EdgeException(String reason, Throwable cause) {
    super(reason, cause);
  }
}
