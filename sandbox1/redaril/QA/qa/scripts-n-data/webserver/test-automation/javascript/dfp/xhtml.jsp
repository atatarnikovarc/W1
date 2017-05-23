<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@page contentType="application/xhtml+xml;charset=UTF-8"%>
<html xmlns="http://www.w3.org/1999/xhtml" lang="en" xml:lang="en">
<head>
</head>
<body>
<h3>Test dfp(xHTML)</h3>
<script type="text/javascript" src="http://qa-10.qa.coreaudience.com/js/dfp.js"></script>
<script type="text/javascript">
//<![CDATA[
var rasegs='';
//defaultKVP:'cas=X',
radfp({ver:1, defaultKVP:'cas=X', id:85416, rnd: Math.random()});
//]]>
</script>
<script>
//<![CDATA[
var div = document.createElement("div");
div.innerHTML = rasegs;
document.body.appendChild(div);
//document.write("<div>" + rasegs + "</div>");
//]]>
</script>
</body>
</html>

