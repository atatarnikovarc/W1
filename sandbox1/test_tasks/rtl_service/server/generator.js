/**
 * Created by atatarnikov on 24.08.16.
 */

var net = require('net'),
    logger = require('./../util/logger')('generator'),
    config = require('./../util/config');

logger.info('Start server');

var usersCount = 100000;

var server = net.createServer(function(socket) {
        socket.end('goodbye\n');
}).on('error', function(err) {
    // handle errors here
    throw err;
});

server.listen({port: config.gen_listen_port}, function() {
    console.log('opened server on', server.address());
});


logger.info('Stop server');