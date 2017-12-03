/**
 * Created by atatarnikov on 22.06.17.
 */

let config = require('./config');
let parseString = require('xml2js').parseString;
let fs = require('fs');

function SongsHub() {
    this.songsOverallList = [];

    this.init();
}

SongsHub.prototype.init = function () {
    let self = this;

    return new Promise((resolve, reject) => {
        fs.readFile(config.lib_path, 'utf8', function (err, contents) {
            if (err) {
                console.log('Songs file reading error');
                reject('cannot read songs file');
            } else {
                parseString(contents, function (err, result) {
                    self.formSongsList(result, resolve);
                });
            }
        });
    });
};

SongsHub.prototype.formSongsList = function (list, resolve) {

    for (let e of list.Library.Artist) {

        for (let a of e.Song) {
            let obj = {};
            obj.name = e.$.name;
            obj.duration = this.convertDuration(a.$.duration);
            obj.id = a.$.id;
            obj.title = a.$.name;
            this.songsOverallList.push(obj);
        }
    }

    resolve();
};

SongsHub.prototype.getRandomId = function () {
    return this.getRandomId_int(this.songsOverallList.length);
};

SongsHub.prototype.getRandomId_int = function (a) {
    return Math.floor(Math.random() * (a));
};

SongsHub.prototype.convertDuration = function (duration) {
    return Math.floor(parseInt(duration, 10) / 1000);
};

//it hangs sometimes(if there is no such songs - stop search)
//count == 3, resulting list is 4
//the rest of the list kind of the same - search songs randomly?
//need to delete group names from songs titles
//create the statistics - letter, count of songs starts with

SongsHub.prototype.getByCount = function (count) {
    // console.log('total songs: ', this.songsOverallList.length);
    //TODO
    //got through the list and form it)
    // this.songsOverallList[0].title.charAt(this.songsOverallList[0].title.length - 1)
    let songs = [];
    songs.push(this.songsOverallList[this.getRandomId()]);

    for (let i = 0; i < count - 1;) {
        let lastChar = this.getLastChar(songs[i].title);
        let wasItFound = false;
        let lastCharSongs = [];
        let sortedLst = this.getSongsStartsWith(lastChar);

        let self = this;

        // songs.forEach(function (e) {
        //     if (!self.isInList(e, sortedLst))
        //         lastCharSongs.push()
        // });

        sortedLst.forEach(function (e) {
            if (!self.isInList(e, songs))
                lastCharSongs.push(e);
        });

        if (lastCharSongs.length != 0) {
            let rID = this.getRandomId_int(lastCharSongs.length);
            songs.push(lastCharSongs[rID]);
            ++i;
            wasItFound = true;

            // for (let j = 0; j < this.songsOverallList.length; j++) {
            //     if (this.songsOverallList[j].title.startsWith(lastChar) &&
            //         !this.isInList(this.songsOverallList[j], songs)) {
            //         songs.push(this.songsOverallList[j]);
            //         ++i;
            //         wasItFound = true;
            //         break;
            //     }
            // }
        }

        if (!wasItFound) {
            console.log('we cannot find the next elm for last char: ', lastChar);
            break;
        }
    }

    // this.checkId();

    return songs;
};

SongsHub.prototype.isInList = function (o, lst) {
    //put here any or some - do return
    for (let e of lst) {
        // console.log('is in list');
        if (e.id == o.id)
            return true;
    }

    return false;
};


//service method just to test input data on duplicates by 'id'
SongsHub.prototype.checkId = function () {
    let self = this;
    let res = [];

    this.songsOverallList.forEach(function (e) {
        let currIndex = 0;

        self.songsOverallList.forEach(function (j) {
            if (e.id == j.id) {
                // console.log(e.id, '--', j.id);
                ++currIndex;
            }
        });

        if (currIndex > 1) {
            console.log(e.id);
            res.push({id: e.id, index: currIndex});
        }
    });

    return res;
};

SongsHub.prototype.getLastChar = function (s) {

    // songs[i].title.charAt(songs[i].title.length - 1)
    for (let i = s.length - 1; i >= 0; i--) {
        let currChar = s.charAt(i);
        // console.log('currChar', currChar);
        // console.log('s', s);
        // console.log('i', i);

        if (this.isLetter(currChar)) {
            return currChar;
        }
    }
};


//TODO: the requirements lack - what we consider as a valid character
SongsHub.prototype.getFirstChar = function (s) {
    for (let i = 0; i < s.length; i++) {
        let currChar = s.charAt(i);
        if (this.isLetter(currChar))
            return currChar;
    }
};

SongsHub.prototype.getSongsStartsWith = function (s) {
    let res = [];
    let self = this;

    this.songsOverallList.forEach(function (e) {
        if (self.getFirstChar(e.title) == s)
            res.push(e);
    });
    return res;
}

SongsHub.prototype.isLetter = function (c) {
    //TODO: temp decision
    return c.toLowerCase() != c.toUpperCase();
};

SongsHub.prototype.getByDuration = function (duration) {
    //TODO
    //got through the list and form it)
};

SongsHub.prototype.getTheShortestList = function () {

};


let hub = new SongsHub();

module.exports = hub;
