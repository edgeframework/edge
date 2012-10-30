load('vertx.js');
load('edge.js');

var log = vertx.logger;

edge
.get('/', function(e,d){
	e.renderText("This is a test");
	e.stop();
})

.get('/:value', function(e){
	log.info(e.params);
	log.info(e);
	e.renderText("Rendering a Value: " + e.param("value"));
	e.stop();
})

.get('/blogs/:name', function(e){
	var name = e.param('name');
	
	if(name == ""){
		e.renderText("Index of Blogs");
	}else{
		e.renderText("Blog Entry : " + name);
	}
	e.stop();
})

.get('*', function(e){
	log.info('404 : ' + e.uri);
	e.stop();
})

.listen(8080,'localhost');