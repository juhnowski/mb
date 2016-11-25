var afMap = {};

function afTrack() {

    $(window).load(function () {
        afPage(false);
    });
    $(window).unload(function () {
        afPage(true);
    })
}

function afPage(leaving) {

    var pageName = document.location.href.match(/[^\/]+$/);
    if (pageName == null) {
        pageName = 'index';
    } else {
        pageName = pageName[0].replace('.shtml', '');
    }

    $.ajax({
        url: "/main/rest/af/page",
        type: "PUT",
        data: JSON.stringify({
            pageName: pageName,
            leaving: leaving
        }),
        contentType: "application/json",
        dataType: "json",
        success: function (resp) {
        }
    });

}

$(window).ready(function () {

    var afSelector = $('input[af-id]');

    afSelector.each(function () {

        var fieldName = $(this).attr('af-id');
        afMap[fieldName] = '';
    });

    afSelector.on('focus', function () {

        if (!$(this).is(':radio')) {

            var fieldName = $(this).attr('af-id');
            var fieldValue = $(this).val();
            afMap[fieldName] = fieldValue;

            console.log('af focus ' + fieldName + ' = ' + fieldValue);
        }
    });

    afSelector.on('change', function () {

        var fieldName = $(this).attr('af-id');
        var oldFieldValue = afMap[fieldName];
        var fieldValue;

        if ($(this).is(':checkbox')) {
            fieldValue = $(this).is(':checked');
        }
        else {
            fieldValue = $(this).val();
        }

        console.log('af change ' + fieldName + ' = ' + oldFieldValue + ' : ' + fieldValue);

        if (oldFieldValue !== '' && oldFieldValue !== fieldValue) {

            $.ajax({
                url: "/main/rest/af/field",
                type: "PUT",
                data: JSON.stringify({
                    fieldName: fieldName,
                    fieldValueBefore: oldFieldValue,
                    fieldValueAfter: fieldValue
                }),
                contentType: "application/json",
                dataType: "json",
                success: function (resp) {
                }
            });
        }

        afMap[fieldName] = fieldValue;
    });
});