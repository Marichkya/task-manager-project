$(document).ready(function () {
    $('#phoneTooltip').tooltip('show')
    addUser();
});

function addUser() {
    $("#submitButton").click(function (e) {

            var loginValid = false;
            if ($("#inputLogin").val() == "") {
                $("#loginText").text("Введіть логін");
                $("#oneStar").attr('class', 'text-danger');
            } else {
                $("#loginText").text("");
                loginValid = true;
            }

            var emailValid = validateEmail($("#inputEmail").val());
            if ($("#inputEmail").val() == "") {
                $("#emailText").text("Введіть адресу електронної пошти");
                $("#twoStar").attr('class', 'text-danger');
            } else {
                if (!emailValid) {
                    $("#emailText").text("Адреса електронної пошти введена неправильно");
                    $("#twoStar").attr('class', 'text-danger');
                } else {
                    $("#emailText").text("");
                }
            }

            var passwordValid = false;
            if ($("#inputPasswordOne").val() === "") {
                $("#passwordTextOne").text("Введіть пароль");
                $("#passwordTextTwo").text("Тут також треба ввести пароль");
                $("#threeStar").attr('class', 'text-danger');
                $("#fourStar").attr('class', 'text-danger');
            } else if ($("#inputPasswordOne").val().length < 6) {
                $("#passwordTextOne").text("Довжина пароля має бути 6 або більше символів");
                $("#threeStar").attr('class', 'text-danger');
                $("#fourStar").attr('class', 'text-danger');
            } else {
                $("#passwordTextOne").text("");
                passwordValid = true;
            }

            if ((($("#inputPasswordOne").val() != "") && ($("#inputPasswordTwo").val() == "")) ||
                ($("#inputPasswordOne").val() != $("#inputPasswordTwo").val())) {
                $("#passwordTextTwo").text("Паролі повинні співпадати");
                $("#threeStar").attr('class', 'text-danger');
                $("#fourStar").attr('class', 'text-danger');
                passwordValid = false;
            } else {
                $("#passwordTextTwo").text("");
            }

            var phoneValid = true;
            if ($("#inputPhone").val() != "") {
                phoneValid = validatePhone($("#inputPhone").val());
                if (!phoneValid) {
                    $("#phoneText").text("Номер телефона введений неправильно");
                } else {
                    $("#phoneText").text("");
                }
            }


            if ((loginValid) && (emailValid) && (passwordValid) && (phoneValid)) {
                var user = {
                    name: $("#inputLogin").val(),
                    username: $("#inputEmail").val(),
                    password: $("#inputPasswordOne").val(),
                    phone: $("#inputPhone").val()
                }

                $.ajax({
                    type: "POST",
                    url: "/user/add",
                    contentType: "application/json",
                    data: JSON.stringify(user),
                    dataType: "json",
                    success: function (result, status, xhr) {
                        if (result.description == "OK") {
                            $("#titleModal").text("Вітаємо!");
                            $("#textModal").append($("<p>").append("Новий користувач успішно зареєстрований"));
                            $("#textModal").append($("<p>").append("На вказану адресу електронної пошти ми відправили лист для завершення реєстрації"));
                            $("#myModal").modal({
                                backdrop: 'static',
                                keyboard: false,
                                show: true,
                                focus: true
                            });
                            $("#closeModal").click(function () {
                                location.reload();
                            })
                        }
                    },
                    error: function (xhr, status, error) {
                        var jsonError = jQuery.parseJSON(xhr.responseText);
                        var desc = (jsonError != "") ? jsonError.description : "Щось пішло не так...";
                        $("#titleModal").text("Упс...");
                        $("#textModal").text(desc);
                        $("#myModal").modal({
                            backdrop: 'static',
                            keyboard: false,
                            show: true,
                            focus: true
                        });
                        $("#closeModal").click(function () {
                            location.reload();
                        })
                    }
                });
            }
        }
    );
}

function validateEmail(param) {
    var re = /^[\w]{1}[\w-\.]*@[\w-]+\.[a-z]{2,4}$/i;
    var myMail = param;
    var valid = re.test(myMail);
    return valid;
}

function validatePhone(param) {
    var re = /^[\+]{1}[\d]{11}[\d]{1}$/m;
    var myPhone = param;
    var valid = re.test(myPhone);
    return valid;
}