/**
 * Created by atatarnikov on 07.10.16.
 */

(function()
{
    var g = function* ()
    {
        yield 1;
        yield 2;
        return 3;
    };

    var t1 = g();
    let t2 = t1.next();
    let t3 = t1.next();
    let t4 = t1.next();
    let t5 = t1.next();
})();
