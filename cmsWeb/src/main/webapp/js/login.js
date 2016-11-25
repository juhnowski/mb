function logout() {
    $.ajax({
        url: "rest/login/logout",
        type: "GET",
        contentType: "application/json",
        success: function (ans) {

        }
    });
}
