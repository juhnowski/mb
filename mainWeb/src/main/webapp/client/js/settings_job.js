app = angular.module('app', ['layout']);

app.controller('SettingsJobCtrl', function ($scope) {
    var api = new API();
    api.getEmploymentData().done(function (data) {
        $scope.peopleData = data;
        $scope.$apply();


        //$scope.isExtCreditSum = data.extCreditSum > 0;
        //$scope.isExtSalary = data.extSalary > 0;


        if (!(data.work.phone != undefined && data.work.phone.value != '')) {
            data.work.phone = {
                value: undefined
            };
        }

        if (data.work != undefined && data.work.fiasAddress != undefined) {
            $('#workAddressFias').address({
                value: data.work.fiasAddress.split(';')
            });
        } else {
            $('#workAddressFias').address({});
        }

        $('#dateStartWork').keyup().mask('99 / 9999');
        // убираем день из даты, интересует только месяц и год
        if ($scope.peopleData.dateStartWork) {
            var dateArr = $scope.peopleData.dateStartWork.split(' / ')
            $scope.dateStartWorkFormatted = dateArr[1] + ' / ' + dateArr[2]
        }


        $('#phoneWork').keyup().mask("+7 (999) 999-99-99? доб.99999");

        $('#typeWorkId').select2("val", $scope.peopleData.typeWorkId);
        $('#organization').select2("val", $scope.peopleData.professionId);
        $('#occupationId').select2("val", $scope.peopleData.occupationId);

        $scope.ai();
    });

    $scope.sendSms = function () {
        if ($scope.jobForm.$invalid ||
            $scope.jobAddressForm.$invalid ||
            $scope.jobAdditionalForm.$invalid) {
            alert('Вы не верно заполнили форму')
            return;
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
            var workFias = $('#workAddressFias');
            var data = angular.copy($scope.peopleData);

            data.smsCode = smsCode.val();
            // собрать данные для отправки
            data.work.fiasAddress = workFias.data('address') ? workFias.data('address').val() : undefined;

            data.dateStartWork = '01 / ' + $scope.dateStartWorkFormatted;


            api.saveEmploymentData(data).done(function () {
                smsError(false);
                $('.alert_box').triggerHandler('click');
                location.reload();
            }).fail(function (resp) {
                smsError(true, resp.errorMessage);
            });
        });

        lkSendKodSms('Подтвердить изменение данных');
    }

    $('#typeWorkId').on('change', function () {
        var select = $(this),
            id = select.val(),
            option = select.find('[value=' + id + ']').text();

        $scope.hideWorkAdditional = (option === 'пенсионер' || option === 'льготный пенсионер' ||
        option === 'неработающий' || option === 'студент/учащийся' || option === 'неработающий');
    });
});

