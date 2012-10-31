load('vertx.js')
load('edge.js');

var log = vertx.logger;
var app = Edge();

app
.get('/', function(req,res){
	log.info('Res: ' + res);
	res.renderText("This is a test");
})

.get('/favicon.ico', function(req,res){
	res.renderText("");
})

.get('/info', function(req,res){
	res.renderText(req.query);
})

.get('/:value', function(req,res){
	res.renderText("Rendering a Value: " + req.param("value"));
})

.get('/blogs/:name', function(req,res){
	var name = req.param('name');

	if(name == ""){
		res.renderText("Index of Blogs");
	}else{
		res.renderText("Blog Entry : " + name);
	}
})

.get('*', function(req,res){
	res
		.status(404)
		.renderText("404");
})

.listen(8080,'localhost');