/**
 * Created by atatarnikov on 23.06.17.
 */

function o1() {
    this.o1_f1 = 0;
}

o1.prototype.m1 = function() {
    let jj = 0;
};

var desc1 = {
    'property1': {
        value: true,
        writable: true
    },
    'property2': {
        value: 'Hello',
        writable: false
    }};

var o2 = Object.create(o1, desc1);

//new vs create?
//what new does?
//what create does?
//how to derive properties

let dest1 = {tt: "asdf"},
    src1 = {t: "v1"},
    src2 = {t: "v2"};

let no = Object.create(dest1);

//no.prototype.new11 = 0;

//no.prototype.new22 = 0;

function oo() {

}

oo.prototype.nn1 = function() {};

let h6 = new oo();

console.log(Object.assign(dest1, src2, src1));
//dest1.prototype.new1 = function() { let i = 0; };
//dest1.prototype.new2 = 44;
dest1.__proto__.new3 = function(t, s) { let a = 9; };

//Object instanceof dest1 - throws an exception, visa-verca - ok

//dest1.prototype - is undefined and oo.prototype - is ok
//function object has 'prototype', other object - no

for (let v in dest1) {
    console.log(v);
    for (let k in v) {
        console.log(k);
    }
}


console.log(1);

var o1 = { a: 1 };
var o2 = { [Symbol('foo')]: 2 };

var obj = Object.assign({}, o1, o2);
console.log(obj); // { a: 1, [Symbol("foo")]: 2 }

var obj = {
    foo: 1,
    get bar() {
        return 2;
    }
};

var copy = Object.assign({}, obj);
console.log(copy);

var v1 = '123';
var v2 = true;
var v3 = 10;
var v4 = Symbol('foo');

var obj = Object.assign({}, v1, null, v2, undefined, v3, v4);
// Примитивы будут обёрнуты, а null и undefined - проигнорированы.
// Обратите внимание, что собственные перечисляемые свойства имеет только обёртка над строкой.
console.log('ddd', obj); // { "0": "1", "1": "2", "2": "3" }