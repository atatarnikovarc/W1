/**
 * Created by atatarnikov on 17.02.17.
 */

function b1() {
    this.a1 = 9;
    console.log('b1: ', this.a1);
}

b1.prototype.nf = function () {
    console.log(1);
    var i = 0;
};

(function () {
    this.a1 = 8;
    var bb = new b1();
    bb.nf();
    b1.bind(this)();
    console.log('ano: ', this.a1);
})();

