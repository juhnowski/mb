app = angular.module('app', [
    'ngSanitize',
    'layout',
    'lk.directives'
]);


app.controller('ProlongCtrl', function ($scope, $http) {
    var local = $scope.local = {
        agreementActual: true,
        longDays: undefined
    };

    $http.get('/main/rest/prolong/info').then(function (resp) {
        $scope.info = resp.data;
        local.agreement = resp.data.prolong.agreement;
        local.longDays = resp.data.prolong.longDays;
    });

    $scope.$watch('local.longDays', function (longDays) {
        if (longDays) {
            local.agreementActual = false;

            var info = $scope.info,
                creditSum = info.credit.sumMainRemain,
                creditStake = info.credit.creditPercent || 0;

            local.sumOldStake = info.sumOldStake;

            local.dateEnd = new Date(info.dateEnd);
            local.dateEnd.setDate(local.dateEnd.getDate() + longDays);
            local.dateEnd = local.dateEnd.getDate() + '/' + (local.dateEnd.getMonth() + 1) + '/' + local.dateEnd.getFullYear();

            local.sumStake = Math.round((longDays + info.daysf) * creditStake * creditSum);

            local.sumAll = local.sumStake + creditSum;

            $http.put('/main/rest/prolong/agreement', {
                sumAll: $scope.local.sumAll,
                longDays: $scope.local.longDays
            }).then(function (resp) {
                local.agreement = resp.data.agreement;
                local.agreementActual = true;
            });
        }
    });

    $scope.sendSms = function () {
        prolongSms({
            sumAll: $scope.local.sumAll,
            longDays: $scope.local.longDays
        });
    };
});


function prolongSms(data) {
    var smsSend = '/main/rest/prolong/sms/send';
    var smsCheck = '/main/rest/prolong/save';


    lkSendKodSms('Подтвердить продление займа');
    request(smsSend, 'put', JSON.stringify(data), function (resp) {
        //$('#smsNumber').text('+' + resp.msisdn);

        $('#retrySendSms').on('click', function () {
            request(smsSend, 'put', JSON.stringify(data))
        });

        $('#confirmSmsCode').on('click', function () {
            data.smsCode = $('#phoneCode').val();

            request(smsCheck, 'post', JSON.stringify(data),
                function (resp) {
                    $('.send-sms_up #phoneCode').parent().removeClass('send-data-server');
                    if (resp) {
                        if (resp.success) {
                            smsError(false);
                            location.href = 'repay.shtml';
                            //up_window.filter('.lk-send-sms_up').hide();
                            //alert_top(up_window.filter('.lk-prolong-success'));
                            //setTimeout(function() {
                            //    location.href = 'repay.shtml'
                            //}, 10000);
                        } else {
                            smsError(true, resp.msg);
                            //$('.send-sms_up #phoneCode').parent().nextAll('.errorMessage').first().text(resp.msg).show();
                            //$('#smsConfirmError').text(resp && resp.msg || resp);
                            //$('#smsConfirmError').closest('.row').removeClass('hide');
                        }
                    }
                });
            //, function (resp) {
            //    alert(resp.msg)

            //$('#smsConfirmError').text(resp && resp.msg || resp);
            //$('#smsConfirmError').closest('.row').removeClass('hide');
            //})
        });

        // тестовое поле кода
        //var inputCode = $('.send-sms_up #phoneCode');
        //inputCode.unbind('keyup');
        //inputCode.on('keyup', function () {
        //    if (inputCode.val().length == inputCode.attr('maxlength')) {
        //        $('.send-sms_up #phoneCode').blur().parent().addClass('send-data-server');
        //        $('#confirmSmsCode').trigger('click');
        //    }
        //});
    })
}
