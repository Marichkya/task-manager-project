$(document).ready(function () {
    var urlParams = getUrlParams();
    getTasklistDetails(urlParams.id);
    loadPages($("#listMyTasks"), "/task/tasklist-tasks?id=" + urlParams.id + "&page=", $("#pagesMyTasks"),
        "/count/tasks-for-tasklist?id=" + urlParams.id, true);
    loadTasks($("#listMyTasks"), "/task/tasklist-tasks?id=" + urlParams.id + "&page=", 0, true);
    deleteTasklist(urlParams.id);
    changeTasklist(urlParams.id);
    formSelectTasks();
    addTaskToTasklist(urlParams.id);
    deleteTaskFromTasklist(urlParams.id);
});

function getTasklistDetails(id) {
    $.getJSON("/tasklist/details?id=" + id, function (data) {
        $("#name").text(data.name);
        $("#description").text(data.description);
    });
}

function deleteTasklist(id) {
    $("#deleteTasklistButton").click(function () {
        var toDelete = {"id": id};
        $.post("/tasklist/delete", toDelete, function (data, status) {
            window.location = "main.html";
        });
    });
}

function changeTasklist(listId) {
    $('#changeTasklistButton').click(function () {
            var newTasklist = {
                id: listId,
                name: null,
                description: null
            };

            if ($("#tasklistName").val() != "")
                newTasklist.name = $("#tasklistName").val();

            if ($("#tasklistDescription").val() != "")
                newTasklist.description = $("#tasklistDescription").val();

            $.ajax({
                type: "POST",
                url: "/tasklist/change",
                contentType: "application/json",
                data: JSON.stringify(newTasklist),
                dataType: "json",
                success: function (result, status, xhr) {
                    if (result.description == "OK") {
                        $("#changeTasklistMessageSpan").text("Список успішно змінено");
                        setTimeout(function () {
                            location.reload();
                        }, 2000);
                    } else {
                        $("#changeTasklistMessageSpan").text("Упс... Щось пішло не так...");
                    }
                },
                error: function (xhr, status, error) {
                    $("#changeTasklistMessageSpan").text("Упс... Щось пішло не так...");
                }
            });
        }
    );
}

function formSelectTasks() {
    $.getJSON("/task/for-select", function (data) {
        var select = $("#selectTask");
        for (i = 0; i < data.length; i++) {

            var option = $("<option>");
            option.val(data[i].id);
            option.text(data[i].title);
            select.append(option);
        }
    });

    $("#selectTask").select2(
        {
            width: "inherit"
        }
    );
}

function addTaskToTasklist(listId) {
    $("#addTaskToTasklistButton").click(function () {

        $.ajax({
            type: "POST",
            url: "/task/add-to-tasklist?taskId=" + $("#selectTask").val(),
            contentType: "application/json",
            data: JSON.stringify(listId),
            dataType: "json",
            success: function (result, status, xhr) {
                $("#addTaskToTasklistMessageSpan").text("Задача добавлена в список");
                setTimeout(function () {
                    location.reload();
                }, 1500);
            },
            error: function (xhr, status, error) {
                var jsonError = jQuery.parseJSON(xhr.responseText);
                var desc = (jsonError != "") ? jsonError.description : "no details";
                $("#addTaskToTasklistMessageSpan").text("Упс... " + desc);
            }
        });
    });
    $("#closeTaskToTasklistButton").click(function () {
        location.reload();
    })
}

function deleteTaskFromTasklist(listId) {
    $("#deleteTaskFromTasklistButton").click(function () {
        var tasksId = [];

        $("input:checked").each(function () {
            tasksId.push($(this).val());
        });

        $.ajax({
            type: "POST",
            url: "/task/delete-from-tasklist?listId=" + listId,
            contentType: "application/json",
            data: JSON.stringify(tasksId),
            dataType: "json",
            success: function (result, status, xhr) {
                location.reload();
            }
        });
    });
}