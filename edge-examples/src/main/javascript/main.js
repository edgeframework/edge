// run edge-tests/src/javascript/main/main.js -cp bin;edge-tests/src/javascript/main/

load('vertx.js')
load('edge.js');

var log = vertx.logger;
var app = Edge();

app

.get('/', function(req,res){
	res.renderText("This is the index page");
})

.listen(8080,'localhost');