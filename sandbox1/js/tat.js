/**
 * Created by atatarnikov on 17.02.16.
 */

var oo = function() {
    this.k = 1;
    var i = 2;
}

var o1 = new oo();
var o2 = Object.create(oo);

//- Assume most recent language standard are available (ES5, C11, C++11...).
//- We expect good performance.
//- If missing more requirements details, just make reasonable assumptions of
//your own.
//- Solution must be simple and compact.
//    No defensive coding, no comments, no unrequested features.
//    Only one file 10-20 lines of code
//- Work only inside Google Docs: no external editor/IDE/debugger, no copy-paste
//to/from such an editor. We must see the flow of how you write the code.
//
//
//    Implement function check (text) which checks whether brackets within text are
//correctly nested. You need to consider brackets of three kinds: (), [], {}.


//no model in my head for the algorithm - have feeling it relates to closed brackets odd checks
var check = function(text) {
    var result = false;
        openbracket = ['(', '[', '{'],
        closedbracket = [')', ']', '}'];

    if (text == '')
        return true;

    for (var i = 0; i < text.length; i++) {
        var currBracketIdx = openbracket.indexOf(text[i]);
        if ( currBracketIdx != -1) {
            var currClosedBracketCount = 0;
            for (var j = i + 1; j < text.length; j++) {
                if (text[j] == closedbracket[currBracketIdx])
                    ++currClosedBracketCount;
            }
            if (currClosedBracketCount == 1)
                result = true
            else
                result = false;
            //what is correct bracket
            //1. first - open, last - closed
        }
    }

    return result;
}//check function

//console.log(check(''));
console.log(check('a(b)'));


//Examples:
//
//check("a(b)") -> true
//check("[{}]") -> true
//check("[(]") -> false
//check("[(])") -> false
//check("}{") -> false
//check("z([{}-()]{a})") -> true
//check("") -> true



//Problem
//===========
//Simplify the implementation below as much as you can.
//    Even better if you can also improve performance as part of the simplification!
//    FYI: This code is over 35 lines and over 300 tokens, but it can be written in
//5 lines and in less than 60 tokens.

function func(s, a, b)
{
    var match_empty=/^$/ ;
    if (s.matcha(match_empty))
    {
        return -1;
    }
    else
    {
        var i=0;
        var aIndex=-1;
        var bIndex=-1;
        while ((aIndex==-1) && (bIndex==-1) && (i<s.length))
        {
            if (s.substring(i, i+1) == a)
                aIndex=i;
            if (s.substring(i, i+1) == b)
                bIndex=i;
            i++;
        }
        if (aIndex != -1)
        {
            if (bIndex == -1)
                return aIndex;
            else
                return Min(aIndex, bIndex);
        }
        else
        {
            if (bIndex != -1)
                return bIndex;
            else
                return -1;
        }
    }//if(s.matcha())
};

function func(s, a, b) {
    var aIndex = s.indexOf(a), bIndex = s.indexOf(b), match_empty=/^$/;
    if (s.matcha(match_empty))
        return -1;
    else if ((aIndex != -1) && (bIndex == -1))
    	return aIndex;
    else if ((aIndex != -1) && (bIndex != -1))
    	return Min(aIndex, bIndex);
    else if ((aIndex == -1) && (bIndex != -1))
    	return bIndex;
    else if ((aIndex == -1) && (bIndex == -1))
    	return -1;
}



