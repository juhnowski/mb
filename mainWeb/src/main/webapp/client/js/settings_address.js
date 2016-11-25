app = angular.module('app', ['layout']);

app.controller('SettingsAddressCtrl', function ($scope) {
	var api = new API();
    api.getAddressData().done(function (data) {
        $scope.peopleData = data;
        $scope.$apply();


        //if (!(data.registration.phone != undefined && data.registration.phone.value != '')) {
        //    data.registration.phone.value = undefined;
        //    $scope.isNotPhoneReg = true
        //} else {
        //    $scope.isNotPhoneReg = false
        //}

        if (!(data.residence.phone != undefined && data.residence.phone.value != '')) {
            data.residence.phone = {
                value: undefined
            };
            $scope.isNotPhoneRes = true
        } else {
            $scope.isNotPhoneRes = false
        }


        if (data.registration.fiasAddress != undefined) {
            $('#registrFias').address({
                value: data.registration.fiasAddress.split(';')
            });
        } else {
            $('#registrFias').address({});
        }

        if (data.residence.fiasAddress != undefined) {
            $('#residentFias').address({
                value: data.residence.fiasAddress.split(';')
            });
        } else {
            $('#residentFias').address({});
        }


        // убираем день из даты, интересует только месяц и год
        var realtyDateArr = $scope.peopleData.misc.realtyDate.split(' / ')
        $('#realtyDate').keyup().mask('99 / 9999');
        $scope.realtyDateFormatted = realtyDateArr[1] + ' / ' + realtyDateArr[2]

        $('#realty').select2('val', $scope.peopleData.misc.realty);
        $('input[type=tel]').keyup().mask('+7 (999) 999 99 99');


        $scope.ai();
    });

    $scope.sendSms = function () {
        if (!$scope.registrationAddressForm.$valid ||
            !$scope.residenceAddressForm.$valid ||
            !$scope.additionalAddressForm.$valid) {
            alert('Вы не верно заполнили форму')
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
            var registrationsFias = $('#registrFias');
            var residenceFias = $('#residentFias');
            var data = angular.copy($scope.peopleData);

            data.smsCode = smsCode.val();

            // собрать данные для отправки
            data.registration.fiasAddress = registrationsFias.data('address') ? registrationsFias.data('address').val() : undefined;
            data.residence.fiasAddress = residenceFias.data('address') ? residenceFias.data('address').val() : undefined;

            data.misc.realtyDate = '01 / ' + $scope.realtyDateFormatted;


            api.saveAddressData(data).done(function () {
                smsError(false);
                $('.alert_box').triggerHandler('click');
                location.reload();
            }).fail(function (resp) {
                smsError(true, resp.errorMessage);
            });
        });

        lkSendKodSms('Подтвердить изменение данных');
    }
});

