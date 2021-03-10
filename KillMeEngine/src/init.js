const express = require("express");
const server = express();
const bodyParser = require('body-parser');
const cookieParser = require('cookie-parser');
const routes = require("./routes");

server.use(cookieParser());
server.use(bodyParser.json());
server.use(bodyParser.urlencoded({ extended: true }));

server.set("view engine", "pug");
server.set('views', './views');

server.use('/data', express.static(__dirname + "/data"));
server.use('/css', express.static(__dirname + "/css"));
server.use('/js', express.static(__dirname + "/js"));
server.use('/img', express.static(__dirname + "/img"));

server.use("/", routes);
server.listen(3000, ()=>{ // Запуск
    console.log("Server started at http://localhost:3000")
});