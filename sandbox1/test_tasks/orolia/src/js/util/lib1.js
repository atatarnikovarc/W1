/**
 * Created by atatarnikov on 10.02.17.
 */

'use strict';

var fs = require('fs');

function formClientList(clients)
{
    var result = [];

    for (var key in clients) {
        if (clients.hasOwnProperty(key))
        {
            result.push(key);
        }
    }

    return JSON.stringify(result);
}

module.exports.formClientList = formClientList;