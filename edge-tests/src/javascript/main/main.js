load('vertx.js');
load('edge.js');

var log = vertx.logger;

edge
.get('/', function(e,d){
	e.renderText("This is a test");
	e.stop();
})

.get('*', function(e){
	log.info('404 : ' + e.uri);
	e.stop();
})

.listen(8080,'localhost');