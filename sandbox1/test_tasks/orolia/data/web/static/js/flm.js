/**
 * Created by atatarnikov on 05.02.17.
 */

'use strict';

function httpGet(theUrl)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.open( "GET", theUrl, false ); // false for synchronous request
    xmlHttp.send( null );
    return xmlHttp.responseText;
}

function httpGetAsync(theUrl, callback)
{
    var xmlHttp = new XMLHttpRequest();
    xmlHttp.onreadystatechange = function() {
        if (xmlHttp.readyState == 4 && xmlHttp.status == 200)
            callback(xmlHttp.responseText);
    }
    xmlHttp.open("GET", theUrl, true); // true for asynchronous
    xmlHttp.send(null);
}

function send_http_del(url, data)
{
    $.ajax({
        type: "DELETE",
        url: url,
        data: data,
        contentType: "application/json",
        success: function(msg){
            console.log("Data Deleted: ", msg);
        }
    });
}

var socket = new WebSocket("ws://localhost:8081");

socket.onmessage = function(event) {
    var cmd = JSON.parse(event.data);
    if (cmd.command == 'delete')
    {
        var elm_to_del = $( "a:contains('" + cmd.name + "')" );
        if (elm_to_del.length != 0)
            elm_to_del[0].parentNode.removeChild(elm_to_del[0]);
    }
    else if (cmd.command == 'add')
    {
        var list_item = document.createElement('a');
        list_item.textContent = cmd.name;
        list_item.setAttribute('class', 'list-group-item');
        list_item.setAttribute('href', '#');
        list_item.addEventListener('click', function() {
            on_item_select(this.textContent);
        });
        document.getElementById('files_list').appendChild(list_item);
    }
};

function on_load()
{
    httpGetAsync('/files/', function(content) {
            var list = JSON.parse(content);
            for (var elm in list)
            {
                var list_item = document.createElement('a');
                list_item.textContent = list[elm];
                list_item.setAttribute('class', 'list-group-item');
                list_item.setAttribute('href', '#');
                list_item.addEventListener('click', function() {
                    makeActive(this);
                    on_item_select(this.textContent);
                });
                document.getElementById('files_list').appendChild(list_item);
            }
        }
    );
}


document.addEventListener("DOMContentLoaded", on_load);

function makeActive(active_elm)
{
    var div_list = document.getElementById('files_list');

    var active_elm_list = div_list.getElementsByClassName('active');
    if (active_elm_list.length != 0)
        active_elm_list[0].classList.remove('active');
    active_elm.className += ' active';
}

var X = [], Y = [], dots = [];

function on_item_select(name)
{
    httpGetAsync('/files/?name=' + name, function(content) {
        var split_by_ns = content.split('\n');

        //remove '#' strings
        var i = 0;
        for (; i < split_by_ns.length; i++)
        {
            if (!split_by_ns[i].startsWith('#'))
            {
                break;
            }
        }

        split_by_ns.splice(0, i);

        for (var elm of split_by_ns)
        {
            var split_by_space = elm.split(' ');
            if (split_by_space.length == 2)
            {
                dots.push([Number(split_by_space[0]), Number(split_by_space[1])]);
                //X.push(Number(split_by_space[0]));
                //Y.push(Number(split_by_space[1]));
            }
        }
        plotter();
    });
}

function plotter() {

    //var plot_list = document.getElementsByClassName("plotly");
    //if (plot_list.length == 1)
    //    plot_list[0].parentNode.removeChild(plot_list[0]);
    var graphDiv = document.getElementById('plot');
    //Plotly.purge(graphDiv);

    //try {
    //    Plotly.deleteTraces(graphDiv, 0);
    //} catch (e) {}
    //
    //var trace = [{ x: X, y: Y } ];
    ////var layout = { margin: { t: 0 } };
    //Plotly.addTraces(graphDiv, trace);
    $.plot($("#plot"), [ dots ], {});
}

function on_delete(name)
{

    var div_list = document.getElementById('files_list');
    var active_elm_list = div_list.getElementsByClassName('active');
    var name_to_del;

    //we do have selected one
    if (active_elm_list.length != 0)
    {
        name_to_del = active_elm_list[0].textContent;
        active_elm_list[0].parentNode.removeChild(active_elm_list[0]);
        var cmd = {command: "delete", name: name_to_del};
        send_http_del('/files/', JSON.stringify(cmd));
    }
}

document.getElementById('delete_button').addEventListener('click', on_delete);

//implemented by design - should be implemented if no refresh of the whole page
function on_upload(name)
{
    //add to the list
    //send put request to the server {command: add, name: value}
}