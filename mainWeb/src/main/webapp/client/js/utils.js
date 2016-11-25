function parseGetParams(key) {
    var str2 = window.location.search;
    var arr1 = str2.split("?");
    if (arr1.length == 2) {
        var arr2 = arr1[1].split("&");
        for (var i = 0; i < arr2.length; i++) {
            var getVar = arr2[i].split("=");
            if (getVar[0] == key) {
                return getVar[1];
            }
        }
    }
    return "";
}

function dataHideblock(t, show) {
    z = $('.hideblock-' + t.attr('data-hideblock'));
    show ? z.slideDown() : z.slideUp();
}

function checkBoxChange(elem) {
    $(elem).closest('.col').prev().andSelf().removeClass('error');

    var show = $(elem).prop('checked') ? true : false;
    if ($(elem).attr('data-hideblock-invert') == '') show = !show;
    dataHideblock($(elem), show);

    if ($(elem).attr('type') == 'radio')
        $('[name="' + $(elem).attr('name') + '"]').not($(elem)).each(function () {
            var show = $(elem).attr('data-hideblock-invert') == '' ? true : false;
            dataHideblock($(elem), show);
        });
}
