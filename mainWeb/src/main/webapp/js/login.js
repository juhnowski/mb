$window.ready(function () {

    $('.login_up .btn').on('click', function () {

        $.ajax({
            url: "/main/rest/login",
            data: JSON.stringify([$('#login').val(), $('#password').val()]),
            type: "POST",
            contentType: "application/json",
            dataType: "json",
            success: function (ans) {
                if (ans.status) {
                    valid_vertif($('#login'), "", true);
                    valid_vertif($('#password'), "", true);
                    token = getToken(ans.link);
                    self.location = getParameterByName("redirectTo") || "/main/client/overview.shtml";
                } else {
                    //valid_vertif($('#login'), "", false);
                    valid_vertif($('#password'), "Неверные email или пароль", false);
                }
            }
        });
    });
});

function valid_vertif(input, text, valid) {
    var errBlock = input.nextAll('.errorMessage:first');
    if (!valid) {
        $(errBlock).text(text);
        $(errBlock).show();
    } else {
        $(errBlock).hide();
    }

}
