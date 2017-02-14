/**
 * Created by atatarnikov on 31.01.17.
 */

'use strict';

var config  = require('./util/config');
var multer = require('multer');
var upload = multer({ dest: config.web_upload_dir });
var bodyParser = require('body-parser');
var express = require('express');
var app = express();
var fs = require('fs');
var favicon = require('serve-favicon');
var lib1 = require('./util/lib1');

//file under processing
var file_list = {};
var clients = {};

require('./file_observer')(file_list);
require('./ws_server')(clients);

app.use(express.static(config.web_static_dir));
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({extended: true}));
app.use(favicon(config.web_static_dir + 'img/favicon.ico'));

app.get('/files', function (req, res) {

    if(typeof(req.query.name) == 'undefined')
    {
        res.contentType('application/json');
        res.end(lib1.formClientList(file_list));
    }
    else
    {
        //TODO: compress the file's content
        fs.readFile(req.query.name, function(err, data) {
                if (err) throw err;
                res.end(data);
            }
        );
    }
});

app.post('/files', upload.single('ssd_file'), function (req, res, next) {
    var cmd = {command: "add", name: req.file.path};

    for (var key in clients)
    {
        clients[key].send(JSON.stringify(cmd));
    }
    res.redirect('back');
});

app.delete('/files', function (req, res) {
    fs.unlink(req.body.name, function(e) { console.log(e); });
    var cmd = {command: "delete", name: req.body.name};

    for (var key in clients)
    {
        clients[key].send(JSON.stringify(cmd));
    }
});

app.listen(config.http_port, function () {
    //TODO: add proper logging
});