(function () {
    'use strict';

    window.pageInit = function() {
        $.getJSON("/main/rest/firstrequest/getSumsData").success(function(res) {
            slidersInit(res.data);
        });

        var checkLocationCb = function(evt) {
            evt.preventDefault();

            var self = this;

            $.ajax({
                url: '/main/rest/firstrequest/validate/location',
                method: 'POST',
                dataType: 'json',
                contentType: 'application/json'
            }).success(function(res) {
                if (res.success) {
                    var url = $(self).attr('href') + "?",
                        sum = $('#summ_zaim-input').val(),
                        days = $('#date_zaim-input').val();

                    if (sum) {
                        url += 'initialSum=' + sum;
                    }
                    if (days) {
                        url += '&initialDays=' + days
                    }
                    location.href = url;
                } else {
                    alert_top(up_window.filter('.location_denied_up'));
                    //up_box.off();
                }
            });
        };


        $('#sendToAnketa').on('click', checkLocationCb);
        $('.sendToAnketa').on('click', checkLocationCb);
    };
})(window);
