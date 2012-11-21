// run edge-examples/src/main/javascript/main.js

load('vertx.js')

var log = vertx.logger;
var app = new com.darylteo.edge.core.EdgeApplicationJS();

app

.get('/', function(req,res){
    res.renderTemplate("index", {
        links: [
            {
                name: "Basic Example",
                url: "/examples/basic"
            },
            {
                name: "Basic Example (Multiple Handlers)",
                url: "/examples/multiple"
            },
            {
                name: "POST Example",
                url: "/examples/post"
            },
            {
                name: "Server Error Example",
                url: "/examples/exception"
            }
        ]
    });
})

.get("/examples/basic", function(req,res){
    res.renderTemplate("basic");
})

.get("/examples/multiple", function(req,res,next){
    req.getData().put("pass", "through");
    next();
}, function(req,res){
    res.renderTemplate("basic",{
        pass: "through"
    });
})

.get("/examples/post", function(req,res){
    res.renderTemplate("post");
})
.post("/examples/post", function(req,res){
    res.renderTemplate("post", {
        body: req.body
    });
})

.get("/examples/exception", function(req,res){
    var obj = undefined;
    obj.doSomething();
})

.use(app.bodyParser)

.listen(8080,'localhost');