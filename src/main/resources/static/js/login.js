$(document).ready(function () {
    var params = window.location.href.slice(window.location.href.indexOf("?")).split("=");
    if (params[1] === "true") {
        $("#myModal").modal({
            backdrop: 'static',
            keyboard: false,
            show: true,
            focus: true
        });
    }
});