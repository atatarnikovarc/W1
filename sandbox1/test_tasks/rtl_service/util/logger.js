/**
 * Created by atatarnikov on 27.08.16.
 */

module.exports = function(fn) {
    var log4js = require('log4js');
    log4js.loadAppender('file');
    log4js.addAppender(log4js.appenders.file('./logs/' + fn + '.log'), 'logger1');

    var logger = log4js.getLogger('logger1');
    logger.setLevel('INFO');
    return logger;
}


