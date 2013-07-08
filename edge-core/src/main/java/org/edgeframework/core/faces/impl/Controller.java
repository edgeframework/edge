package org.edgeframework.core.faces.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import org.vertx.java.core.Vertx;

import com.jetdrone.vertx.yoke.middleware.YokeCookie;
import com.jetdrone.vertx.yoke.middleware.YokeRequest;
import com.jetdrone.vertx.yoke.middleware.YokeResponse;

public abstract class Controller {

  /* Persistence Strategies */
  public static abstract class PersistenceStrategy {
    private Map<String, String> existing;

    private Map<String, String> getMap() {
      if (existing != null) {
        return existing;
      }
      existing = createMap();
      return existing;
    }

    abstract Map<String, String> createMap();
  }

  public class Persistence {
    public final PersistenceStrategy IN_MEMORY = new PersistenceStrategy() {
      @Override
      public Map<String, String> createMap() {
        return vertx.sharedData().getMap("edge:session");
      }
    };
  }

  /* Instance Variables */
  private Vertx vertx;
  private ControllerFace face;
  private YokeRequest request;

  private Persistence strategies = new Persistence();
  private PersistenceStrategy sessionStrategy = strategies.IN_MEMORY;

  void setRequest(YokeRequest request) {
    this.request = request;
  }

  void setFace(ControllerFace face) {
    this.face = face;
  }

  void setVertx(Vertx vertx) {
    this.vertx = vertx;
  }

  /* Util Classes */
  protected abstract class Result {
    public abstract void render(YokeResponse response);
  }

  /* Properties */
  protected YokeRequest request() {
    return this.request;
  }

  protected String beginSession() {
    String sessionID = request.getSessionId();

    if (sessionID == null) {
      sessionID = UUID.randomUUID().toString();
      request.setSessionId(sessionID);
    }

    return sessionID;
  }

  protected void endSession() {
    String sessionID = request.getSessionId();

    if (sessionID == null) {
      return;
    }

    // TODO: remove persistence backend
    request.setSessionId(null);
  }

  protected boolean isActiveSession() {
    return request.getSessionId() != null;
  }

  protected Map<String, String> session() {
    // TODO: I'm going to rely heavily on the Yoke sessionID implementation for
    // this
    // If there are any significant security/auth leaking issues then we may
    // relook this.
    String sessionID = request.getSessionId();

    if (sessionID == null) {
      // TODO: provide warning about accessing map
      return Collections.emptyMap();
    }

    // TODO: implement different types of session persistence strategies

    // return the session map for this session
    // if there isn't one, then create one
    return this.sessionStrategy.getMap();
  }

  protected String cookies(String name) {
    List<YokeCookie> cookies = request.getAllCookies(name);

    if (cookies != null) {
      for (YokeCookie cookie : cookies) {
        if (Objects.equals(cookie.getName(), name)) {
          return cookie.getValue();
        }
      }
    }

    return null;
  }

  protected void cookies(String name, String value) {
    YokeCookie cookie = new YokeCookie(name, this.face.getMac());
    cookie.setValue(value);
    request.response().addCookie(cookie);
  }

  /* Result Methods */
  protected Result ok(final String content) {
    return new Result() {
      @Override
      public void render(YokeResponse response) {
        response.setStatusCode(200);
        response.headers().add("content-length", "" + content.length());
        response.write(content);
        response.close();
      }
    };
  }
}
