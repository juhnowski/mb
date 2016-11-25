/**
 * Created by cobalt on 18.12.15.
 */

function login_new_lk() {
    $.ajax({
        url: "/managernew/rest/login",
        data: JSON.stringify([$('#login').val(), $('#password').val(), $('#remember').attr("checked")]),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.status) {
                valid_vertif($('#login'), "", true);
                valid_vertif($('#password'), "", true);
                token = getToken(ans.link);


                $('#login-error').addClass('hide');

                self.location = getParameterByName("redirectTo") || "/managernew/borrower.shtml";
            } else {
                $('#login-error').removeClass('hide');
            }
        }
    });

    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
}

function valid_vertif(input, text, valid) {
    var u = $('<u>').addClass('red');
    if (input.parent().hasClass('bg_box')) {
        if (input.parent().next().is('u'))
            u = input.parent().next();
        else
            input.parent().after(u);
    }
    else if (input.attr('type') == 'checkbox') {
        if (input.parent().siblings('u').is('u'))
            u = input.parent().siblings('u');
        else
            input.parent().parent().append(u);
    }
    else if (input.next().is('u'))
        u = input.next();
    else
        input.after(u);
    u.text(text);
    if (valid)
        input.closest('.l_i').removeClass('required')
    else
        input.closest('.l_i').addClass('required');
}

function getToken(str) {
    var arr = str.split("?tempLink=");
    return arr[1];
}

function logout() {
    $.post("/managernew/rest/auth/logout").then(function () {
        window.location.replace('/managernew/login.shtml');
    });
}