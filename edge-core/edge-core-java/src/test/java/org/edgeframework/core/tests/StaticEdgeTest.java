package org.edgeframework.core.tests;

import org.edgeframework.core.tests.faces.StaticAssets;
import org.edgeframework.core.tests.util.AbstractEdgeTest;
import org.junit.Test;

public class StaticEdgeTest extends AbstractEdgeTest {
  @Test
  public void testGet() {
    deployVerticle(StaticAssets.class.getCanonicalName())
      .then(testContents("localhost", 8080, "/index.html", "Hello World"))
      .fail(onFailure())
      .fin(onComplete());
  }
}
