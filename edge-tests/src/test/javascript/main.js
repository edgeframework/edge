// run edge-examples/src/main/javascript/main.js

load('vertx.js')

var log = vertx.logger;
var app = new com.darylteo.edge.core.EdgeApplicationJS();

app

.get('/', function(req,res){
    res.render("index", {
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
                name: "Server Error (500)",
                url: "/examples/exception"
            },
            {
                name: "File Not Found (404)",
                url: "/examples/random"
            }
        ]
    });
})

.get("/examples/basic", function(req,res){
    res.render("basic");
})

.get("/examples/multiple", function(req,res,next){
    req.getData().put("pass", "through");
    next();
}, function(req,res){
    res.render("basic",{
        pass: "through"
    });
})

.get("/examples/post", function(req,res){
    res.render("post");
})
.post("/examples/post", function(req,res){
    res.render("post", {
        body: req.body
    });
})

.get("/examples/exception", function(req,res){
    var obj = undefined;
    obj.doSomething();
})

.all("*", function(req,res){
    res
        .status(404)
        .render("404");
})

.use(app.assets("public"))
.use(app.bodyParser())

.listen(8080,'localhost');