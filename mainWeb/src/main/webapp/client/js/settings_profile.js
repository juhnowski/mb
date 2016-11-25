app = angular.module('app', ['layout']);
var api = new API();


app.controller('SettingsProfileCtrl', function ($scope) {
    api.getProfileData().done(function (data) {
        $scope.peopleData = data;
        $scope.$apply();

        $('#phone').keyup().mask('+7 (999) 999 99 99');
    });


    $scope.sendSms = function () {
        if ($scope.passwordForm.$invalid ||
            ($scope.peopleData.passwordData.newPassword != $scope.peopleData.passwordData.newPassword2)) {
            alert('Вы не верно заполнили форму');
            return
        }

        var smsCode = $('#phoneCode');
        smsCode.val('');
        smsError(false);


        api.userSendSms({phone: $scope.peopleData.smsNumber});

        $('#smsNumber').html('+' + $scope.peopleData.smsNumber);
        $('#retrySendSms').unbind('click').on('click', function () {
            api.userSendSms({phone: $scope.peopleData.smsNumber});
        });
        $('#confirmSmsCode').unbind('click').on('click', function () {
            var data = angular.copy($scope.peopleData);

            data.smsCode = smsCode.val();

            // собрать данные для отправки
            // собирать нечего, все ок

            api.saveProfileData(data).done(function () {
                smsError(false);
                $('.alert_box').triggerHandler('click');
                location.reload();
            }).fail(function (resp) {
                smsError(true, resp.errorMessage);
            });
        });

        lkSendKodSms('Подтвердить изменение данных');
    }


    $scope.sendCheck = function (type) {
        console.log(type)
    }

    // подтверждение данных с помощью кодов
    $('.confirm-code .confirm__btn').on('click', function () {
        var btn = $(this);
        var row = btn.closest('.confirm-code');
        var value = row.find('.confirm__input-value');
        var input = row.find('.confirm__input-code');
        var timer = row.find('.confirm__timer');

        var url = btn.attr('data-send-url');


        var data = {};
        if (row.hasClass('confirm-code-phone')) {
            data = {phone: value.val()}
        } else if (row.hasClass('confirm-code-email')) {
            data = {email: value.val()}
        }
        $.ajax({
            url: url,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            data: JSON.stringify(data)
        });


        btn.hide();
        input.show();
        timer.show();

        var seconds = btn.attr('data-second');
        timer.text('0:' + seconds);


        var idTimer = setInterval(function () {
            timer.text('0:' + seconds--);
            if (seconds == 0) {
                clearInterval(idTimer);
                timer.hide();

                // если не подтверждено поле значит можно отправить заново
                if (!value.is(':disabled')) {
                    btn.text('отправить заново')
                    btn.show()
                }
            }
        }, 1000);
    });

    $('.confirm-code .confirm__input-code').on('change', function () {
        var input = $(this);
        var row = input.closest('.confirm-code');
        var btn = row.find('.confirm__btn');
        var timer = row.find('.confirm__timer');
        var value = row.find('.confirm__input-value');
        var loadingAnim = row.find('.confirm__loading');
        var message = row.find('.confirm-code__message');

        if (input.val().length != input.attr('maxlength')) {
            return
        }

        btn.hide();
        input.hide();
        timer.hide();
        message.text('');
        loadingAnim.show();

        var url = btn.attr('data-verify-url');
        $.ajax({
            url: url,
            method: 'POST',
            contentType: "application/json; charset=utf-8",
            dataType: 'json',
            data: JSON.stringify({code: input.val()}),
            complete: function () {
                loadingAnim.hide();
            },
            success: function (ans) {
                if (ans.success) {
                    input.unbind('keyup change blur');
                    value.attr('disabled', 'disabled');
                    message.text('подтверждено');

                    if (row.hasClass('confirm-code-phone')) {
                        $scope.peopleData.mainData.smsHash = ans.data;
                        $scope.peopleData.mainData.smsCode = input.val()
                    } else if (row.hasClass('confirm-code-email')) {
                        $scope.peopleData.mainData.emailHash = ans.data;
                        $scope.peopleData.mainData.emailCode = input.val()
                    }
                } else {
                    input.show();

                    // если отправить заново существует значит таймер не показываем
                    if (!btn.is(':visible')) {
                        timer.show();
                    }

                    message.text('код не верный')
                }
            }
        });
    });
});

