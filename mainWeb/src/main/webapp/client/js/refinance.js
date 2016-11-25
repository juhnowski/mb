app = angular.module('app', [
    'ngSanitize',
    'layout',
    'lk.directives'
]);


app.controller('RefinanceCtrl', function ($scope, $http) {
    var local = $scope.local = {
        agreementActual: true,
        refinanceAmount: undefined
    };

    $http.get('/main/rest/refinance/info').then(function (resp) {
        var info = $scope.info = resp.data;
        local.agreement = resp.data.agreement;
        local.dateEnd = new Date();
        local.dateEnd.setDate(local.dateEnd.getDate()+info.refinanceDays);
        local.dateEnd = local.dateEnd.getDate()+' / '+ (local.dateEnd.getMonth() + 1)+' / '+local.dateEnd.getFullYear();
        local.refinanceAmount = resp.data.refinanceAmount;
    });

    $scope.$watch('local.refinanceAmount', function(refinanceAmount) {
        if (refinanceAmount) {
            local.agreementActual = false;

            var info = $scope.info;

            local.sumStake = Math.floor((info.sumAll-refinanceAmount) * (info.stake) * (info.refinanceDays));

            local.sumAll = info.sumAll - refinanceAmount + local.sumStake;

            $http.put('/main/rest/refinance/agreement', {refinanceAmount: refinanceAmount}).then(function (resp) {
                local.agreement = resp.data.agreement;
                local.agreementActual = true;
            });
        }
    });

    $scope.sendSms = function() {
        refinanceSms({
            refinanceAmount: local.refinanceAmount
        });
    };
});


function refinanceSms(data) {
    var smsSend = '/main/rest/refinance/sms/send';
    var smsCheck = '/main/rest/refinance/save';


    lkSendKodSms('Подтвердить рефинансирование займа');
    request(smsSend, 'put', JSON.stringify(data), function (resp) {
        $('#smsNumber').text('+' + resp.msisdn);

        $('#retrySendSms').on('click', function () {
            request(smsSend, 'put', JSON.stringify(data))
        });

        $('#confirmSmsCode').on('click', function () {
            data.smsCode =  $('#phoneCode').val();

            request(smsCheck, 'post', JSON.stringify(data),
                function (resp) {
                    if (resp) {
                        if (resp.success) {
                            location.href = 'repay.shtml';

                            //up_window.filter('.lk-send-sms_up').hide();
                            //alert_top(up_window.filter('.lk-refinance-success'));
                            //setTimeout(function() {
                            //    location.href = 'repay.shtml'
                            //}, 10000);
                        } else {
                            alert(resp.msg)
                            //$('#smsConfirmError').text(resp && resp.msg || resp);
                            //$('#smsConfirmError').closest('.row').removeClass('hide');
                        }
                    }
                },
                function (resp) {
                    alert(resp.msg)
                    //$('#smsConfirmError').text(resp && resp.msg || resp);
                    //$('#smsConfirmError').closest('.row').removeClass('hide');
                })
        });
    })
}
