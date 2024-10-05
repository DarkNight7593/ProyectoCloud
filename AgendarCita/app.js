var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');

// Importar rutas
var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var citasRouter = require('./routes/citas');  // Importamos la nueva ruta de citas

var app = express();

// Configuraciones
app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

// Usar rutas
app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/citas', citasRouter);  // Usamos la nueva ruta de citas

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  res.status(404).send('Ruta no encontrada');
});

// error handler
app.use(function(err, req, res, next) {
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  res.status(err.status || 500);
  res.render('error');
});

module.exports = app;


