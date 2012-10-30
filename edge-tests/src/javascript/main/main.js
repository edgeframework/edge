load('vertx.js');
load('edge.js');

var log = vertx.logger;

edge.get('/', function(e){
	log.info(e);
	e.response.end("This is a test");
}).listen(8080,'localhost');