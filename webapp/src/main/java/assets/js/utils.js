$(function() {

    function clearCookie(name) {
        document.cookie = name + '=;expires=Thu, 01 Jan 1970 00:00:01 GMT;';
    }

    $("a[href='" + window.location.pathname + "']").parent().addClass("active");

    if (document.cookie.indexOf("clear=true") != -1) {
        localStorage.setItem("ninja_cookie", null);
        clearCookie("NINJA_SESSION");
    }
    else if (document.cookie)
        localStorage.setItem("ninja_cookie", document.cookie);
    else if (!document.cookie && localStorage.getItem("ninja_cookie")) {
        document.cookie = localStorage.getItem("ninja_cookie");
        window.location.replace("/");
    }

    /*window.WebSocket = window.WebSocket || window.MozWebSocket;
    if (!window.WebSocket)
        alert("WebSocket not supported by this browser");

    var websocket = new WebSocket("ws://localhost:8080/ws/");
    websocket.onopen = function() {
        console.log("open");
    };
    websocket.onclose = function() {
        console.log("close");
    };
    websocket.onmessage = function(event) {
        console.log("message: " + event.data);
    };*/

});