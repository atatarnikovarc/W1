/**
 * Created by atatarnikov on 08.02.17.
 */

'use strict';

var WebSocketServer = new require('ws');
var config = require('./util/config');

var webSocketServer = new WebSocketServer.Server({
    port: config.ws_port
});

module.exports = function(clients)
{
    webSocketServer.on('connection', function(ws) {

        var id = Math.random();
        clients[id] = ws;

        ws.on('message', function(message) {
        });

        ws.on('close', function() {
            delete clients[id];
        });

    });
}