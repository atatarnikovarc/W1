<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page contentType="application/xhtml+xml;charset=UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
	<script type="text/javascript" src="//p0.raasnet.com:8688/js/dfp_async1.js"></script> 
</head>
<body>
<h3>Test dfp async(xHTML)</h3>

<input type="button" value="RUN" onclick="onButtonClick(); return false;"/>

<script type="text/javascript">
//<![CDATA[
	function destroyChildren(node)
	{
		while (node.firstChild)
      node.removeChild(node.firstChild);
    };

	function target_me() {
		target_me = function() {
			div = document.createElement("div");
			div.innerHTML = "target_me() function call - onclick onTimeout: " + CA.getTargetKeyValues() + " after " + (new Date().getTime() - startTime) + " ms";
			document.body.appendChild(div);
		};
		div = document.createElement("div");
		div.innerHTML = "target_me() function call - onLoad: " + CA.getTargetKeyValues() + " after " + (new Date().getTime() - startTime) + " ms";
		document.body.appendChild(div);
	};
	
	function addDiv(count, timeout, i) {
		i = i || 0;
		div = document.createElement("div");
		div.innerHTML = i + ". Key-value pairs: " + CA.getTargetKeyValues() + " after " + (new Date().getTime() - startTime) + " ms";
		document.body.appendChild(div);
		if (i < count - 1) {
			setTimeout(function() {addDiv(count, timeout, i + 1)}, timeout);
		}
	};
//]]>
</script>

<script type="text/javascript">
//<![CDATA[
	function onButtonClick() {
		startTime = new Date().getTime();
		CA.radfp({id:85729, disableFirstPartyCookies: false, onload: "target_me()", timeout: 4000});
		addDiv(10, 500);
		destroyChildren(document.body);
		document.body.innerHTML = "<h3>Test dfp async(HTML)</h3>";
        document.body.innerHTML = document.body.innerHTML + '<input type="button" value="RUN" onclick="onButtonClick(); return false" />';
		target_me = function() {
			target_me = function() {
				div = document.createElement("div");
				div.innerHTML = "target_me() function call - onclick onTimeout: " + CA.getTargetKeyValues() + " after " + (new Date().getTime() - startTime) + " ms";
				document.body.appendChild(div);
		    };
		    div = document.createElement("div");
		    div.innerHTML = "target_me() function call - onLoad: " + CA.getTargetKeyValues() + " after " + (new Date().getTime() - startTime) + " ms";
		    document.body.appendChild(div);
		};
	}
//]]>    
</script>

</body>
</html>
