/**
 * Created by atatarnikov on 09.08.16.
 */


//Rookie mistake #1: the promisey pyramid of doom

var util = require('util');
const EventEmitter = require('events');

function MyEmitter() {
    EventEmitter.call(this);
}
util.inherits(MyEmitter, EventEmitter);

MyEmitter.prototype.doStuff = function doStuff() {
    console.log('before')
    this.emit('fire')
    console.log('after')
};

var me = new MyEmitter();

me.on('fire', function() {
    console.log('emit fired');
});

me.doStuff();
// Output:
// before
// emit fired
// after