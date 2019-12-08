function goToURL(url) {
    location.href = url;
}

function getUrlParams() {
    var params = [], hash;
    var hashes = window.location.href.slice(window.location.href.indexOf('?') + 1).split('&');
    for (var i = 0; i < hashes.length; i++) {
        hash = hashes[i].split('=');
        params.push(hash[0]);
        params[hash[0]] = hash[1];
    }
    return params;
}

function loadTasks(element, url, page, check) {
    element.empty();

    $.getJSON(url + page, function (data) {
        var rootDiv = element;
        for (var i = 0; i < data.length; i++) {
            var list = document.createElement("ul");
            $(list).attr("class", "list-group list-group-horizontal-sm my-0 mx-0 py-0 px-0");
            if (data[i].complete === false) {
                if (check === true) {
                    $(list).append($("<li>").attr("class", "list-group-item link-list col-5-1 mx-0 pt-1 pb-0").append(
                        $("<input>").attr("type", "checkbox").attr("value", data[i].id)
                    ));
                } else {
                    $(list).append($("<li>").attr("class", "list-group-item link-list col-5-1 mx-0 pt-1 pb-0").append(
                        $("<input>").attr("type", "checkbox").attr("value", data[i].id).attr("disabled", true)
                    ));
                }
                $(list).append(
                    $("<li>").attr("class", "list-group-item link-list col-5-2 mx-0 py-0").append(
                        data[i].title)
                );
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-3 mx-0 py-0").append(
                    data[i].dateToNotify.replace('T', "  ")
                ));
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-4 mx-0 py-0").append(
                    data[i].dateFinish.replace('T', "  ")
                ));
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-5 mx-0 p-0 pl-5"));
            } else {
                if (check === true) {
                    $(list).append($("<li>").attr("class", "list-group-item link-list col-5-1 comlete-task mx-0 pt-1 pb-0").append(
                        $("<input>").attr("type", "checkbox").attr("value", data[i].id)
                    ));
                } else {
                    $(list).append($("<li>").attr("class", "list-group-item link-list col-5-1 comlete-task mx-0 pt-1 pb-0").append(
                        $("<input>").attr("type", "checkbox").attr("value", data[i].id).attr("disabled", true)
                    ));
                }
                $(list).append(
                    $("<li>").attr("class", "list-group-item link-list col-5-2 comlete-task mx-0 py-0").append(
                        data[i].title)
                );
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-3 comlete-task mx-0 py-0").append(
                    data[i].dateToNotify.replace('T', "  ")
                ));
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-4 comlete-task mx-0 py-0").append(
                    data[i].dateFinish.replace('T', "  ")
                ));
                $(list).append($("<li>").attr("class", "list-group-item link-list col-5-5 comlete-task mx-0 p-0 pl-5").append(
                    $("<img>").attr("src", "svg/check.svg").attr("class", "large-icons")
                ));
            }
            var link = document.createElement("a");
            $(link).attr("href", "task.html?id=" + data[i].id).attr("class", "btn-light link-list");
            $(link).append(list);
            rootDiv.append(link);
        }
    });
}

function loadPages(taskElement, taskUrl, pagesElement, pagesUrl, check) {
    pagesElement.empty();

    $.getJSON(pagesUrl, function (data) {
        var pageCount = (data.count / data.pageSize) +
            (data.count % data.pageSize > 0 ? 1 : 0);
        var i;

        for (i = 1; i <= pageCount; i++) {
            pagesElement.append(
                $('<li>').attr('class', 'page-item').append(
                    $('<a>').attr('class', 'page-link').attr('id', i - 1)
                        .append(i))
            );
        }
    });

    pagesElement.on("click", ".page-link", function (event) {
        loadTasks(taskElement, taskUrl, event.target.id, check);
    });
}

function getAuthUser() {
    var authUser = $.ajax({
        url: '/check',
        async: false,
        dataType: 'json',
        success: function (data) {
            return data;
        }
    }).responseText;
    return JSON.parse(authUser);
}

function validateDate(stringDate) {
    var now = new Date();
    var date = new Date(stringDate);
    return ((date.getTime()) > (now.getTime()));
}