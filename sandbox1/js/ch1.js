/**
 * Created by atatarnikov on 01.06.17.
 */


//require('underscore');
var mobx = require('mobx');

var person = mobx.observable({
    firstName: 'Matt',
    lastName: 'Ruby',
    age: 0,
    fullName: function() {
        return this.firstName + ' ' + this.lastName;
    }
});

//mobx getter doesn't work
console.log(person.fullName);

person.firstName = 'Mike';
console.log(person.fullName);

person.firstName = 'Lissy';
console.log(person.fullName);

//_.times()