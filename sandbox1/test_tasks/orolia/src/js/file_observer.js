/**
 * Created by atatarnikov on 04.02.17.
 */
'use strict';

var fs = require('fs');
var chokidar = require('chokidar');
var config = require('./util/config');

var watcher = chokidar.watch(config.web_upload_dir, {
    ignored: /[\/\\]\./, persistent: true
});

var log = console.log.bind(console);

module.exports = function(files) {
    watcher
        .on('add', function (path) {
            fs.readFile(path, function (err, data) {
                if (err) throw err;
                files[path] = data;
                //TODO: send all clients notify
                //log('added: ', path);
            });
        })
        .on('unlink', function (path) {
            //TODO: send all clients notify
            delete files[path];
            //log('deleted: ', path, '===', files);
        })
        .on('error', function (error) {
            log('Error happened', error);
        });
};