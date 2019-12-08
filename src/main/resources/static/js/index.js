$(document).ready(function () {
    checkAutheUser();

    loadTaskHighPriorityData(0);
    loadTaskHighPriorityPages();
});

function checkAutheUser() {
    $.getJSON('/check', function (data) {
        if (data.name != null) {
            $("#data").attr('hidden', false);
            $("#nav").attr('hidden', false);
            $("#needUl").prepend('<li class="nav-item active">\n' +
                '<a class="btn btn-success" href="main.html" vi>Особистий кабінет</a>\n' +
                '</li>');
            $("#message").text("Вихід");
            $("#url").attr("href", "/logout");
            $("#helloMessage").text("Привіт, " + data.name);
        } else {
            $("#figure").attr('hidden', false);
            $("#message").text("Вхід");
            $("#url").attr("href", "/login");
            $("#needUl").append('<li class="nav-item active">\n' +
                '<a class="btn btn-success" href="registration.html">Зареєструватись</a>\n' +
                '</li>');
            $("#helloMessage").text("Ласкаво просимо, Гість");
        }

    });
}

function loadTaskHighPriorityData(page) {
    $("#listMyTasks").empty();

    $.getJSON("/task/high-priority?page=" + page, function (data) {
        var rootDiv = $("#listMyTasks");
        for (var i = 0; i < data.length; i++) {
            var list = document.createElement("ul");
            $(list).attr("class", "list-group list-group-horizontal-sm my-0 mx-0 py-0 px-0");
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-1 mx-0 py-0").text(data[i].title));
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-2 mx-0 py-0").text(data[i].dateFinish.replace("T", "  ")));
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-3 mx-0 py-0").append(
                $("<span>").attr("class", "badge badge-danger badge-pill mx-3 px-3 py-1 my-list-item").text(data[i].priority))
            );
            var link = document.createElement("a");
            $(link).attr("href", "task.html?id=" + data[i].id).attr("class", "btn-light link-list");
            $(link).append(list);
            rootDiv.append(link);
        }
    });
}

function loadTaskHighPriorityPages() {
    $("#pagesMyTasks").empty();

    $.getJSON("/count/tasks-high-priority", function (data) {
        var pageCount = (data.count / data.pageSize) +
            (data.count % data.pageSize > 0 ? 1 : 0);
        var i;

        for (i = 1; i <= pageCount; i++) {
            $('#pagesMyTasks').append(
                $('<li>').attr('class', 'page-item').append(
                    $('<a>').attr('class', 'page-link').attr('id', i - 1)
                        .append(i))
            );
        }
    });

    $("#pagesMyTasks").on("click", ".page-link", function (event) {
        loadTaskHighPriorityData(event.target.id);
    });
}