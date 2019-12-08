$(document).ready(function () {
    var authUser = getAuthUser();
    var urlParams = getUrlParams();
    getTaskDetails(urlParams.id, authUser);
    getAllUsersForFriends(urlParams.id);
    deleteTask(urlParams.id);
    closeTask(urlParams.id);
    changeTask(urlParams.id);
    addComment(urlParams.id, authUser);
    loadCommentsData(0, urlParams.id);
    loadCommentsPages(urlParams.id);
    addTaskToTasklist(urlParams.id);
    formSelectTasklists(urlParams.id);
    shareTask(urlParams.id);
});

function getTaskDetails(taskId, authUser) {
    $.getJSON('/task/details?id=' + taskId, function (data) {
        var task = data.taskDTO;
        var taskAuthor = data.authorTask;
        var friends = data.taskFriends;
        $("#title").text(task.title);
        $("#description").text(task.description);
        if (task.priority === "Високий") {
            $("#priority").attr("class", "alert alert-danger").append(task.priority);
        } else if (task.priority === "Середній") {
            $("#priority").attr("class", "alert alert-warning").append(task.priority);
        } else {
            $("#priority").attr("class", "alert alert-success").append(task.priority);
        }
        $("#dateNotification").text(task.dateToNotify.replace('T', '  '));
        $("#dateFinishInfo").text(task.dateFinish.replace('T', '  '));
        $("#author").attr("value", taskAuthor.username).text(taskAuthor.name);

        var i;
        for (i = 0; i < friends.length; i++) {
            if (friends[i].username === authUser.username) {
                continue;
            }
            var div = $("<div>");

            var button = $("<button>")
                .attr("type", "button")
                .attr("class", "btn btn-outline-secondary px-0 py-0 ml-1 mb-2")
                .attr("value", friends[i].id);


            if (authUser.username === taskAuthor.username) {
                var img = $("<img>")
                    .attr("src", "svg/x.svg")
                    .attr("alt", "x")
                    .attr("class", "small-icons")
                    .on("click", {taskId: taskId}, deleteFriend);
                button.append(img);
            }

            div.append(friends[i].name)
            div.append(button);
            $("#friends").append(div);
        }

        $("#listName").text(task.listName);
        if (task.complete) {
            $("#status").attr('class', 'alert alert-success').append("Виконана");
        } else {
            $("#status").attr('class', 'alert alert-warning').append("В роботі");
        }

        if (authUser.username !== taskAuthor.username) {
            $("#deleteTaskButton")
                .removeAttr("class")
                .attr("class", "btn btn-outline-secondary")
                .attr("disabled", true);

            $("#shareModal")
                .removeAttr("class")
                .attr("class", "btn btn-outline-secondary")
                .attr("disabled", true);

            $("#changeModal")
                .removeAttr("class")
                .attr("class", "btn btn-outline-secondary")
                .attr("disabled", true);
        }

    });
}

function shareTask(id) {
    $("#shareTaskButton").click(function () {
        var result = $("#selectFriends").val();
        var friends = [];
        if (result != null) {
            for (var i = 0; i < result.length; i++) {
                var tempArr = result[i].split(";");
                var tempFriendsTask = {name: tempArr[0], username: tempArr[1]};
                friends.push(tempFriendsTask);
            }
        }

        $.ajax({
            type: "POST",
            url: "/task/share?id=" + id,
            contentType: "application/json",
            data: JSON.stringify(friends),
            dataType: "json",
            success: function (result, status, xhr) {
                if (result.description == "OK") {
                    $("#taskMessageSpan").text("Ви поділились задачею з вибраними користувачами");
                    $.ajax({
                        type: "POST",
                        url: "send/shared-task-notification?id=" + id,
                        contentType: "application/json",
                        data: JSON.stringify(friends),
                        dataType: "json"
                    });
                    setTimeout(function () {
                        location.reload();
                    }, 1500);
                }
            },
            error: function (xhr, status, error) {
                $("#taskMessageSpan").text("Упс... Щось пішло не так");
                setTimeout(function () {
                    location.reload();
                }, 1500);
            }
        });
    });
}

function changeTask(id) {
    $('#changeTaskButton').click(function () {

            var changedTask = {
                id: id,
                title: null,
                description: null,
                dateFinish: null,
                dateToNotify: null,
                priority: null
            };

            var dateValidOne = true;
            if ($("#dateFinish").val() !== "") {
                dateValidOne = validateDate($("#dateFinish").val());
                if (!dateValidOne) {
                    $("#dateFinishSpan").text("Машину часу поки не придумали)");
                } else {
                    $("#dateFinishSpan").text("");
                    dateValidOne = true;
                }
            }


            var dateValidTwo = true;
            if ($("#dateToNotify").val() !== "") {
                dateValidTwo = validateDate($("#dateToNotify").val());
                if (!dateValidOne) {
                    $("#dateToNotifySpan").text("Машину часу поки не придумали)");
                } else {
                    $("#dateToNotifySpan").text("");
                    dateValidTwo = true;
                }
            }

            if ((dateValidOne) && (dateValidTwo)) {

                if ($("#taskTitle").val() != "")
                    changedTask.title = $("#taskTitle").val();

                if ($("#taskText").val() != "")
                    changedTask.description = $("#taskText").val();

                if ($("#finishDate").val() != "")
                    changedTask.dateFinish = $("#dateFinish").val();

                if ($("#notifyDate").val() != "")
                    changedTask.dateToNotify = $("#dateToNotify").val();

                if ($("#taskPriority").val() != "")
                    changedTask.priority = $("#taskPriority").val();

                $.ajax({
                    type: "POST",
                    url: "/task/change",
                    contentType: "application/json",
                    data: JSON.stringify(changedTask),
                    dataType: "json",
                    success: function (result, status, xhr) {
                        if (result.description == "OK") {
                            $("#changeTaskMessageSpan").text("Задача успішно змінена");
                            setTimeout(function () {
                                location.reload();
                            }, 2000);
                        }
                    },
                    error: function () {
                        $("#changeTaskMessageSpan").text("Упс... Щось пішло не так...");
                    }
                });

            }

        }
    )
    ;
}

function closeTask(id) {
    $('#closeTaskButton').click(function () {
        var taskId = {'id': id}
        $.post("/task/close", taskId, function (data, status) {
            location.reload();
        });
    });
}

function deleteTask(id) {
    $("#deleteTaskButton").click(function () {
        var tasksId = {'toDelete': []};
        tasksId['toDelete'].push(id);
        $.post("/task/delete", tasksId, function (data, status) {
            window.location = "main.html";
        });
    });
}

function addComment(taskId, authUser) {
    $('#addCommentButton').click(function () {
            if ($("#inputComment").val() == "") {
                $("#commentHelp").text("Напишіть що-небудь");
            } else {
                var comment = {
                    textComment: $("#inputComment").val(),
                    taskId: taskId,
                    authorName: "",
                    dateCreate: new Date(),
                };

                $.ajax({
                    type: "POST",
                    url: "/comment/add",
                    contentType: "application/json",
                    data: JSON.stringify(comment),
                    dataType: "json",
                    success: function (result) {
                        if (result.description == "OK") {
                            location.reload();
                            if (authUser.username !== $("#author").attr("value")) {
                                $.getJSON("/send/comment-notification?id=" + taskId);
                            }
                            $.getJSON("/send/comment-notification-for-friends?id=" + taskId);
                        }
                    }
                });
            }
        }
    );
}

function loadCommentsData(page, taskId) {
    $('#commentsList > :last-child').empty();

    $.getJSON('/comment/all?page=' + page + '&taskId=' + taskId, function (data) {
        var i;
        for (i = 0; i < data.length; i++) {
            var comment = document.createElement("div");
            var child = document.createElement("div");
            $(child).attr('class', 'alert alert-primary').attr('role', 'alert');
            $(child).append($('<div>').attr('class', 'row pb-2').append(
                $('<div>').attr('class', 'col').append(
                    $('<div>').attr('class', 'float-right').append(
                        $('<span>').attr('class', 'badge badge-dark').attr('id', 'authorName').append(
                            data[i].dateCreate.replace('T', '  ') + "   " + data[i].authorName)
                    )
                )
                )
            );
            $(child).append(
                $('<div>').attr('class', 'row pb-2').append(
                    $('<div>').attr('class', 'col').append(
                        $('<div>').attr('class', 'float-right').append(data[i].textComment)
                    )
                )
            );
            $(comment).append(child);
            $('#commentsList > :last-child').append(
                comment
            );
        }
    });
}

function loadCommentsPages(taskId) {
    $("#pagesComments").empty();

    $.getJSON('/count/comments?taskId=' + taskId, function (data) {
        var pageCount = (data.count / data.pageSize) +
            (data.count % data.pageSize > 0 ? 1 : 0);
        var i;

        for (i = 1; i <= pageCount; i++) {
            $('#pagesComments').append(
                $('<li>').attr('class', 'page-item').append(
                    $('<a>').attr('class', 'page-link').attr('id', i - 1)
                        .append(i))
            );
        }
    });

    $("#pagesComments").on("click", ".page-link", function (event) {
        loadCommentsData(event.target.id, taskId);
    });
}

function formSelectTasklists() {
    $.getJSON("/tasklist/for-select", function (data) {
        var select = $("#selectList");
        for (var i = 0; i < data.length; i++) {
            var option = $("<option>");
            option.val(data[i].id);
            option.text(data[i].name);
            select.append(option);
        }

        $("#selectList").select2(
            {
                width: "inherit"
            }
        );
    });
}

function addTaskToTasklist(taskId) {
    $("#addTaskToTasklistButton").click(function () {
        $.ajax({
            type: "POST",
            url: "/task/add-to-tasklist?taskId=" + taskId,
            contentType: "application/json",
            data: JSON.stringify($("#selectList").val()),
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


function deleteFriend(event) {
    var taskId = event.data.taskId;
    var friendId = $(this).parent().val();

    $.ajax({
        type: "POST",
        url: "/user/delete-friend?taskId=" + taskId,
        contentType: "application/json",
        data: JSON.stringify(friendId),
        dataType: "json",
        success: function () {
            location.reload();
        },
        error: function () {
            location.reload();
        }
    });
}

function getAllUsersForFriends(id) {
    $("#selectFriends").empty();

    $.getJSON("/user/for-friends-list?id=" + id, function (data) {
        var select = $("#selectFriends");
        var size = data.length;
        var i;
        for (i = 0; i < size; i++) {

            var option = $("<option>");
            option.val(data[i].name + ';' + data[i].username);
            option.text(data[i].name);
            select.append(option);
        }

        $("#selectFriends").select2(
            {
                width: "inherit"
            }
        );
    });
}