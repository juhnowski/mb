function getListPayment() {
    var tokenl = parseGetParams('link');
    $.ajax({
        url: "/main/rest/listpayment/getListPayment",
        data: JSON.stringify({token: tokenl}),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                if (ans.data != null) {
                    console.log(ans.data)


                    if ((!ans.data.hasCredit)) {
                        $("#creditBlock").show();
                    }
                    if (ans.data.hasCredit && (ans.data.lstPay.length == 0)) {
                        $("#noPayBlock").show();
                    }
                    if (ans.data.hasCredit && (ans.data.lstPay.length > 0)) {
                        var $tbody = $('<tbody></tbody>');
                        $.each(ans.data.lstPay, function() {
                            var $tr = $('<tr></tr>');

                            $('<td>' + this.createDate + '</td>').appendTo($tr);
                            $('<td>' + this.processDate + '</td>').appendTo($tr);
                            $('<td>' + this.amount + '</td>').appendTo($tr);
                            $('<td>' + this.partner_realName + '</td>').appendTo($tr);

                            $tr.appendTo($tbody);
                        });

                        var $list = $("#lstPay");
                        $list.show();
                        $list.find('tbody').replaceWith($tbody);
                    }
                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getLstSched() {
    var tokenl = parseGetParams('link');
    $.ajax({
        url: "/main/rest/listpayment/getLstSched",
        data: JSON.stringify({token: tokenl}),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans.data);
            if (ans.success) {
                if (ans.data != null) {

                    if (ans.data.length == 0) {
                        $("#no-list").show();
                    } else {
                        $('#table-list').show();

                        var newContent = '';
                        $.each(ans.data, function () {
                            newContent = newContent +
                                ('<tr>' +
                                '<td>' + this.databeg + '</td>' +
                                '<td>' + this.dataend + '</td>' +
                                '<td>' + this.creditsumback + '</td>' +
                                '<td>' + this.active + '</td>' +
                                '<td>' + this.reasonEnd + '</td>' +
                                '</tr>');
                        });

                        $(newContent).appendTo("#table-list tbody");
                    }
                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getHistoryCredits() {
    var tokenl = parseGetParams('link');
    $.ajax({
        url: "/main/rest/listpayment/getHistory",
        data: JSON.stringify({token: tokenl}),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans.data);
            if (ans.success) {
                if (ans.data != null) {

                    if (ans.data.credit.length == 0 && ans.data.creditReq.length == 0) {
                        $("#no-list").show();
                    }
                    if (ans.data.credit.length > 0) {
                        $('#table-list').show();

                        var newContent = '';
                        $.each(ans.data.credit, function () {
                            newContent = newContent +
                                ('<tr>' +
                                '<td onclick="getCreditData(' + this.id + ')"><b><span class="orang">' + this.creditAccount + '</span></b></td>' +
                                '<td><b>' + this.creditsum + '</b></td>' +
                                '<td><b>' + this.creditdatabeg + '</b></td>' +
                                '<td><b>' + this.creditdataendfact + '</b></td>' +
                                '</tr>');
                        });

                        $(newContent).appendTo("#table-list tbody");
                    }
                    if (ans.data.creditReq.length > 0) {
                        $('#table-list2').show();

                        var newContent = '';
                        $.each(ans.data.creditReq, function () {
                            newContent = newContent +
                                ('<tr>' +
                                '<td><b>' + this.uniquenomer + '</span></b></td>' +
                                '<td><b>' + this.datecontest + '</b></td>' +
                                '<td><b>' + this.creditsum + '</b></td>' +
                                '<td><b>' + this.creditdays + '</b></td>' +
                                '<td><b>' + this.status + '</b></td>' +
                                '</tr>');
                        });

                        $(newContent).appendTo("#table-list2 tbody");
                    }
                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

function getCreditData(id) {
    $.ajax({
        url: "/main/rest/listpayment/getCreditData",
        data: JSON.stringify({creditId: id}),
        type: "POST",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            console.log(ans.data);
            if (ans.success) {
                if (ans.data != null) {

                    for (i in ans.data) {
                        $('.alert_up.view').find('#' + i).text(ans.data[i]);
                    }

                    alert_top($('.alert_up.view'));
                }
            } else {
                alert("false " + ans.errorMessage);
            }
        }
    });
}

