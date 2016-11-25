var CAN_ADD;

function initCanAdd() {
    if (CAN_ADD == undefined) {
        initData(function(){
            makeCanAdd()
        });
    } else {
        makeCanAdd();
    }
}

function initData(callback) {
    $.ajax({
        url: "/main/rest/ai/getData",
        type: "GET",
        contentType: "application/json",
        dataType: "json",
        success: function (ans) {
            if (ans.success) {
                console.log(ans.data)

                CAN_ADD = ans.data.canAdd;

                callback();

            } else {
                console.log("false " + ans.errorMessage);
            }
        }
    });
}

function makeCanAdd() {
    if (CAN_ADD == true) {
        $('[data-canadd="true"]').removeClass('hide');
        $('[data-canadd="false"]').hide();
    } else {
        $('[data-canadd-msg="true"]').removeClass('hide');
        $('[data-canadd="false"]').show();
    }
}


