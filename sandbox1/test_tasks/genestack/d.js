/**
 * Created by atatarnikov on 20.10.16.
 */


let a = 332;

for (e of a.toString()) {
    console.log(e);
    console.log(parseInt(e));
}


/*
 * Complete the function below.
 */
function countHoles(num) {
    let no_holes = [1, 2, 3, 5, 7], //do we need it actually?
        one_hole = [0, 4, 6, 9],
        two_hole = [8],
        total_holes = 0;

    for (e of num.toString()) {
        let int_e = parseInt(e);
        if (one_hole.indexOf(int_e) != -1)
            total_holes += 1;
        else if (two_hole.indexOf(int_e) != -1)
            total_holes += 2;
    }

    return total_holes;
}

function maxDifference(a) {
    let max = 0, max_index = -1;

    for (let i = 0; i < a.length; i++) {
        if (a[i] > max) {
            max = a[i];
            max_index = i;
        }
    }

    let min = a[max_index - 1], min_index = -1;

    for (let j = 0; j < max_index; j++) {
        if (a[j] < min) min = a[j];
    }

    return max - min;
}

function gemstones(rocks) {
    let result = -1, liter = '';

    for (let i = 1; i < rocks.length; i++) {
        for (e of rocks[i]) {
            let isFound = false;
            for (let j = i + 1; j < rocks.length; j++) {
                if (rocks[j].indexOf(e) != -1)
                    isFound = true;
                else
                    isFound = false;
            }
            if (isFound) {
                liter = e;
                break;
            }
        }

        break; // )
    }

    result = liter.charCodeAt(0) - 96;
    if (result == 'NaN') result = 0; // it is pity - hence it doesn't work and fails 2-nd test ((

    return result;
}

