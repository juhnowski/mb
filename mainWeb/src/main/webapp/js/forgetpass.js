$window.ready(function () {

    $('.pass_recowery_up .btn').on('click', function () {
        var email = $('#email');
        if (email.val().length === 0) {
            valid_vertifPass(email, "Введите электронный адрес", false);
            return false;
        }

        if (!testEmail(email)) {
            valid_vertifPass(email, "Неверный формат электронного адреса", false);
            return false;
        }
        $.ajax({
            url: "/main/rest/remind-by-email",
            data: email.val(),
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            beforeSend: function () {
            },
            complete: function () {
            },
            success: function (ans) {
                if (ans.status) {
                    //valid_vertifPass(email, "Код выслан на указанный email. Через 10 секунд вы будете перенаправлены на страницу ввода кода.", true);
                    //$('.pass_recowery_up .btn').hide();
                    //setTimeout(function () {
                    valid_vertifPass(email, "", true);
                    alert_top(up_window.filter('.pass_new_up'));
                    //}, 10000);
                } else {
                    valid_vertifPass(email, ans.error, false);
                }
            }
        });
    });

    $('.pass_new_up .btn').on('click', function () {

        var code = $('#emailCode');
        var newPassword1 = $('#newPassword1');
        var newPassword2 = $('#newPassword2');

        var formData = {
            code: code.val(),
            newPassword1: newPassword1.val(),
            newPassword2: newPassword2.val()
        };
        if (validate()) {
            $.ajax({
                url: "/main/rest/remind-confirm",
                data: JSON.stringify(formData),
                type: "POST",
                contentType: "application/json",
                dataType: "json",
                beforeSend: function () {
                },
                complete: function () {
                },
                success: function (ans) {
                    if (ans.status) {
                        //valid_vertifPass(newPassword2, "Пароль успешно изменен. Через 10 секунд вы будете перенаправлены на страницу логина.", true);
                        $('.pass_new_up .btn').hide();
                        //setTimeout(function () {
                        alert_top(up_window.filter('.login_up'));
                        //}, 10000);
                    } else {
                        valid_vertifPass(newPassword2, ans.error, false);
                    }
                }
            });
        }
    });

});

function validate() {
    var pass1 = $('.pass_new_up #newPassword1');
    var pass2 = $('.pass_new_up #newPassword2');

    var error = $('<p>').addClass('error');
    $('.pass_recowery_up p.error').remove();

    var text;
    if (pass1.val().length < 5 || pass1.val() != pass2.val()) {
        pass1.val().length < 5 ? text = 'Пароль должен быть не менее 5 символов' : text = 'Пароли не совпадают';
        valid_vertifPass(pass2, text, false);
        return false;
    }
    return true;
}

function valid_vertifPass(input, text, valid) {
    var errBlock = input.nextAll('.errorMessage:first');
    if (!valid) {
        $(errBlock).text(text);
        $(errBlock).show();
    } else {
        $(errBlock).hide();
    }
}