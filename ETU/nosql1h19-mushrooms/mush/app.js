const createError = require('http-errors');
const express = require('express');
const path = require('path');
const cookieParser = require('cookie-parser');
const bodyParser = require('body-parser');
const logger = require('morgan');
const passport = require('passport');
const session = require('cookie-session');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'pug');

app.use(logger('dev'));

app.use(bodyParser.json({limit:'100mb'}));
app.use(bodyParser.urlencoded({ extended: true, limit:'100mb' }));

app.use(express.json({}));
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser('123'));

app.use(express.static(path.join(__dirname, 'public')));

//Login
require('./login').init(app);

app.use(session({
    secret: '123', //TODO add generator
    resave: true,
    saveUninitialized: true
}));

app.use(passport.initialize());
app.use(passport.session());

//Routes
let indexRouter = require('./routes/index');
let usersRouter = require('./routes/users');
let dbRouter = require('./routes/db');
app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/db', dbRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;
