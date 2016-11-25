app = angular.module('app', ['layout']);

app.controller('SettingsPersonalCtrl', function ($scope) {
	var api = new API();
    api.getPersonalData().done(function (data) {
        $scope.peopleData = data;
        $scope.$apply();


        $('#passportSeries').keyup().mask('9999');
        $('#passportNumber').keyup().mask('999999');
        $('#passportGivenDate').keyup().mask('31 / 12 / 9999');
        $('#passportOrgCode').keyup().mask('999-999');
        $("#gender").select2("val", $scope.peopleData.mainData.gender);
        $('#marriage').select2("val", $scope.peopleData.miscData.marriage);
        $('#education').select2("val", $scope.peopleData.employmentData.educationId);

        $scope.ai();
    });

    $scope.sendSms = function () {
        if (!$scope.mainForm.$valid ||
            !$scope.passportForm.$valid ||
            !$scope.familyForm.$valid ||
            !$scope.educationForm.$valid) {
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
            var data = angular.copy($scope.peopleData);

            data.smsCode = smsCode.val();
            // собрать данные для отравки
            // собирать нечего, все должно быть ок

            api.savePersonalData(data).done(function () {
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
