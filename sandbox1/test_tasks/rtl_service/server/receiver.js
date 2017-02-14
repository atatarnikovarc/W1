/**
 * Created by atatarnikov on 24.08.16.
 */

var logger = require('./../util/logger')('receiver'),
    config = require('./../util/config');

logger.info('Start server');

var server = net.createServer(function(socket) {
    socket.end('goodbye\n');
}).on('error', function(err) {
    // handle errors here
    throw err;
});

// grab a random port.
server.listen({port: config.receiver_listen_port}, function() {
    console.log('opened server on', server.address());
});

logger.info('Stop server');