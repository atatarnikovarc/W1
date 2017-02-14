'use strict';

var config = {};

///////// SERVER  ===================

config.protocol = 'http';
config.host = 'localhost';
config.http_port = '3500';
config.ws_port = '8081';
config.base_url = config.protocol + '://' + config.host + ':' + config.port;
config.page_url =  config.base_url + '/?page=';


config.web_static_dir = './data/web/static/';
config.web_upload_dir = './data/SampleFiles/';


module.exports = config;