/**
 * Created by atatarnikov on 22.06.17.
 */

var config = require('./config');
var hub = require('./songsHub');

var nopt = require("nopt"),
    knownOpts = {
        "count": [String, null],
        "length": [Number, null]
    },
    shortHands = {};

function run() {
    let parsed = nopt(knownOpts, shortHands, process.argv, 2);

    if (parsed.count != undefined)
        setTimeout(() => {
            console.log(hub.getByCount(parsed.count));
        }, 1000);
    else if (parsed.length != undefined)
        console.log(hub.getByDuration(parsed.length));
    else
        console.log(hub.getByCount(config.default_pl_length));
}

run();
