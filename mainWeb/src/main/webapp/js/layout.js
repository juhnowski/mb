app = angular.module('layout', []);


app.controller('MainCtrl', function ($scope, $http) {
    $scope.callback = {};
    $scope.callbackBtnDisable = false;

    $scope.fold = function (id) {
        $('#' + id).toggle('fold')
    };

    $scope.isInvalid = function (field) {
        return field.$dirty && field.$invalid;
    };

    $scope.saveCallback = function (callback) {
        $scope.callbackBtnDisable = true;

        if ($scope.callbackForm.$invalid) {
            $scope.callbackBtnDisable = false;
            return
        }

        var recaptcha = grecaptcha.getResponse(window.recaptchaCallback);
        if (recaptcha == '') {
            $scope.callbackCaptchaValid = false;
            $scope.callbackBtnDisable = false;
            return
        }

        var recaptchaData = {
            response: recaptcha
        };

        $http.post('/main/rest/recaptcha', recaptchaData).then(function (resp) {
            if (resp.success == false) {
                console.log(resp);
                $scope.callbackCaptchaValid = false;
                $scope.callbackBtnDisable = false;
                return
            }

            $scope.callbackCaptchaValid = true;

            $http.post('/main/rest/callback/add', callback).then(function (resp) {
                if (resp.data.success == false) {
                    console.log(resp);
                    $scope.callbackBtnDisable = false;
                    return
                }

                $scope.callback = {};
                $scope.callbackForm.$setPristine();
                grecaptcha.reset(window.recaptchaCallback);
                $scope.callbackBtnDisable = false;
                $('.alert_box').triggerHandler('click');
            })
        });
    };

    $('.callback_up [type=tel]').on('change blur', function () {
        $scope.callback.phone = $(this).val();
        $scope.callbackForm.phone.$dirty = true
    });
});
