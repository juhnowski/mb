function getSumsdata() {
    $.ajax({
        url: "/main/rest/listsums/getData",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)
                if (ans.data != null) {

                    if (ans.data.in.length == 0 && ans.data.out.length == 0) {
                        $('#messBlock').show();
                    } else {
                        if (ans.data.in.length > 0) {
                            $('#lstPay').show();

                            var newContent = '';
                            $.each(ans.data.in, function () {
                                newContent = newContent +
                                    ('<tr>' +
                                    '<td>' + this.creditAccount + '</td>' +
                                    '<td>' + this.amount + '</td>' +
                                    '<td>' + this.eventDate + '</td>' +
                                    '<td>' + this.operationType + '</td>' +
                                    '</tr>');
                            });

                            $(newContent).appendTo("#lstPay table tbody");
                        }
                        if (ans.data.out.length > 0) {
                            $('#lstOutPay').show();

                            var newContent = '';
                            $.each(ans.data.out, function () {
                                newContent = newContent +
                                    ('<tr>' +
                                    '<td>' + this.creditAccount + '</td>' +
                                    '<td>' + this.amount + '</td>' +
                                    '<td>' + this.eventDate + '</td>' +
                                    '<td>' + this.operationType + '</td>' +
                                    '</tr>');
                            });

                            $(newContent).appendTo("#lstOutPay table tbody");
                        }
                    }

                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}
