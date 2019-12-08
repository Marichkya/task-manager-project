$(document).ready(function () {
    var authUser = getAuthUser();
    checkActiveUser(authUser);

    loadPages($("#listMyTasks"), "/task/all?page=", $("#pagesMyTasks"), "/count/tasks", true);
    loadTasks($("#listMyTasks"), "/task/all?page=", 0, true);

    loadPages($("#listFriendsTasks"), "/task/tasks-friends?page=",
        $("#pagesFriendsTasks"), "/count/tasks-friends", false);
    loadTasks($("#listFriendsTasks"), "/task/tasks-friends?page=", 0, false);

    loadTasklistsData(0);
    loadTasklistsPages();

    addTask();
    addTasklist();
    deleteTask();
});

function checkActiveUser(authUser) {
    $("#helloMessage").text("Привіт, " + authUser.name);
    if (authUser.activationCode != null) {
        $("#activateBlock").attr("hidden", false);
        $("#activateText").append("Для використання функціональності сервіса необхідно завершити реєстрацію")
        $("#settings")
            .removeAttr("class")
            .attr("class", "btn btn-outline-secondary")
            .attr("disabled", true);
        $("#settingsDropdown")
            .removeAttr("class")
            .attr("class", "btn btn-outline-secondary")
            .attr("disabled", true);
        $("#addTasklist")
            .removeAttr("class")
            .attr("class", "btn btn-outline-secondary")
            .attr("disabled", true);
        $("#addTask")
            .removeAttr("class")
            .attr("class", "btn btn-outline-secondary")
            .attr("disabled", true);
        $("#deleteTaskButton")
            .removeAttr("class")
            .attr("class", "btn btn-outline-secondary")
            .attr("disabled", true);
    }
}

function addTask() {
    $("#addTaskButton").click(function (e) {
        if ($("#taskTitle").val() == "")
            $("#titleSpan").text("Введіть заголовок");
        else
            $("#titleSpan").text("");

        if ($("#taskText").val() == "")
            $("#textSpan").text("Введіть опис");
        else
            $("#textSpan").text("");

        var dateValidOne = validateDate($("#dateFinish").val());
        if ($("#dateFinish").val() === "") {
            $("#dateFinishSpan").text("Введіть дату і час завершення");
        } else if (!dateValidOne) {
            $("#dateFinishSpan").text("Машину часу поки ще не придумали)");
        } else {
            $("#dateFinishSpan").text("");
            dateValidOne = true;
        }

        var dateValidTwo = validateDate($("#dateToNotify").val());
        if ($("#dateToNotify").val() === "") {
            $("#dateToNotifySpan").text("Введить дату і час сповіщення");
        } else if (!dateValidTwo) {
            $("#dateToNotifySpan").text("Машину часу поки ще не придумали)");
        } else {
            $("#dateToNotifySpan").text("");
            dateValidTwo = true;
        }

        if ($("#taskPriority").val() == "")
            $("#taskPrioritySpan").text("Виберіть пріоритет");
        else
            $("#taskPrioritySpan").text("");

        if (($("#taskText").val() !== "") && (dateValidOne) && (dateValidTwo) && ($("#taskPriority").val() !== "")) {

            var task = {
                title: $("#taskTitle").val(),
                description: $("#taskText").val(),
                dateFinish: $("#dateFinish").val(),
                dateToNotify: $("#dateToNotify").val(),
                priority: $("#taskPriority").val()
            };

            $.ajax({
                type: "POST",
                url: "/task/add",
                contentType: "application/json",
                data: JSON.stringify(task),
                dataType: "json",
                success: function (result, status, xhr) {
                    if (result.description == "OK") {
                        $("#taskMessageSpan").text("Нова задача успішно добавлена");
                        setTimeout(function () {
                            location.reload();
                        }, 1500);
                    }
                },
                error: function (xhr, status, error) {
                    var jsonError = jQuery.parseJSON(xhr.responseText);
                    var desc = (jsonError != "") ? jsonError.description : "no details";
                    $("#taskMessageSpan").text("Упс... Щось пішло не так");
                    setTimeout(function () {
                        location.reload();
                    }, 1500);
                }
            });
        }

        $("#closeTaskButton").click(function () {
            location.reload();
        })
    });
}

function deleteTask() {
    $('#deleteTaskButton').click(function () {
        var tasksId = {'toDelete': []};

        $("input:checked").each(function () {
            tasksId['toDelete'].push($(this).val());
        });

        $.post("/task/delete", tasksId, function (data, status) {
            location.reload();
        });
    });
}

function addTasklist() {
    $("#addListButton").click(function (e) {
        if ($("#listName").val() == "")
            $("#listNameText").text("Введіть назву списку");
        else
            $("#listNameText").text("");

        if ($("#listName").val() != "") {

            var taskslist = {
                name: $("#listName").val(),
                description: $("#listDescription").val()
            };

            $.ajax({
                type: "POST",
                url: "/tasklist/add",
                contentType: "application/json",
                data: JSON.stringify(taskslist),
                dataType: "json",
                success: function (result, status, xhr) {
                    if (result.description == "OK") {
                        $("#listMessageSpan").text("Новий список задач успішно добавлений");
                        setTimeout(function () {
                            location.reload();
                        }, 1500);
                    } else {
                        $("#listMessageSpan").text("Упс... Щось пішло не так");
                        setTimeout(function () {
                            location.reload();
                        }, 1500);
                    }
                },
                error: function (xhr, status, error) {
                    var jsonError = jQuery.parseJSON(xhr.responseText);
                    var desc = (jsonError != "") ? jsonError.description : "no details";

                    $("#listMessageSpan").text("Result: " + status + " " + error + " " +
                        xhr.status + " " + xhr.statusText + ": " + desc);
                }
            });
        }
    });
}

function loadTasklistsData(page) {
    $("#listTasksLists").empty();

    $.getJSON('/tasklist/all?page=' + page, function (data) {
        var rootDiv = $("#listTasksLists");
        for (var i = 0; i < data.length; i++) {
            var list = document.createElement("ul");
            $(list).attr("class", "list-group list-group-horizontal-sm my-0 mx-0 py-0 px-0");
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-1 mx-0 py-0").text(data[i].name));
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-2 mx-0 py-0").text(data[i].description));
            $(list).append($("<li>").attr("class", "list-group-item link-list col-3-3 mx-0 py-0").append(
                $("<span>").attr("class", "badge badge-warning badge-pill mx-3 px-3 py-1 my-list-item").text(data[i].taskCounter))
            );
            var link = document.createElement("a");
            $(link).attr("href", "tasklist.html?id=" + data[i].id).attr("class", "btn-light link-list");
            $(link).append(list);
            rootDiv.append(link);
        }
    });
}

function loadTasklistsPages() {
    $("#pagesTasksList").empty();

    $.getJSON('/count/tasklists', function (data) {
        var pageCount = (data.count / data.pageSize) +
            (data.count % data.pageSize > 0 ? 1 : 0);
        var i;

        for (i = 1; i <= pageCount; i++) {
            $('#pagesTasksList').append(
                $('<li>').attr('class', 'page-item').append(
                    $('<a>').attr('class', 'page-link').attr('id', i - 1)
                        .append(i))
            );
        }
    });

    $("#pagesTasksList").on("click", ".page-link", function (event) {
        loadTasklistsData(event.target.id);
    });
}
