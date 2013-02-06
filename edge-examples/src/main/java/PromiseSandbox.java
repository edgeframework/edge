import org.edgeframework.promises.Promise;
import org.edgeframework.promises.PromiseHandler;
import org.edgeframework.promises.RepromiseHandler;
import org.vertx.java.core.AsyncResult;
import org.vertx.java.core.AsyncResultHandler;
import org.vertx.java.core.Handler;
import org.vertx.java.core.buffer.Buffer;
import org.vertx.java.core.file.AsyncFile;
import org.vertx.java.deploy.Verticle;

public class PromiseSandbox extends Verticle {

  private PromiseSandbox that = this;

  @Override
  public void start() throws Exception {
    System.out.println("start");

    this.openFile()
        .then(new RepromiseHandler<AsyncFile, Buffer>() {
          @Override
          public Promise<Buffer> handle(AsyncFile file) {
            System.out.println("read");

            BytesLoadedHandler closeFile = new BytesLoadedHandler(file);

            that.getFirst10Bytes(file)
                .then(closeFile);

            return closeFile.getBufferPromise();
          }
        })
        .then(new PromiseHandler<Buffer, Void>() {

          @Override
          public Void handle(Buffer value) {
            System.out.println("loaded " + value);
            return null;
          }

        });
  }

  private Promise<AsyncFile> openFile() {
    return Promise.defer(new Handler<Promise<AsyncFile>>() {
      @Override
      public void handle(final Promise<AsyncFile> promise) {
        System.out.println("1. promise handle");
        getVertx().fileSystem().open("build.gradle", new AsyncResultHandler<AsyncFile>() {
          @Override
          public void handle(AsyncResult<AsyncFile> file) {
            promise.fulfill(file.result);
          }
        });
      }
    });
  }

  private Promise<Buffer> getFirst10Bytes(AsyncFile file) {
    final Promise<Buffer> result = Promise.defer();
    final Buffer buffer = new Buffer(20);

    file.read(buffer, 0, 0, 20, new AsyncResultHandler<Buffer>() {
      @Override
      public void handle(AsyncResult<Buffer> read) {
        System.out.println("First 20 Bytes read");

        buffer.appendBuffer(read.result);

        result.fulfill(buffer);
      }
    });

    return result;
  }

  private class BytesLoadedHandler implements PromiseHandler<Buffer, Void> {
    private AsyncFile file;
    private Promise<Buffer> promise = Promise.defer();

    public BytesLoadedHandler(AsyncFile file) {
      this.file = file;
    }

    @Override
    public Void handle(Buffer buffer) {
      System.out.println("Close");
      file.close();

      this.promise.fulfill(buffer);
      return null;
    }

    public Promise<Buffer> getBufferPromise() {
      return this.promise;
    }
  }
}