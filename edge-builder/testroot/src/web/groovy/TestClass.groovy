import static org.vertx.groovy.core.streams.Pump.createPump

println 'Starting Groovy!'

vertx.createNetServer().connectHandler { socket ->
  createPump(socket, socket).start()
}.listen(1234)

def vertxStop() {
  println 'Stopping Groovy!'
}