app = angular.module('app', ['layout']);


app.controller('ContactCtrl', function ($scope, $http) {
    $scope.contact = {
        clientType: 'borrower',
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        message: ''
    };
    $scope.sendBtnDisable = false;


    $scope.isInvalid = function (field) {
        return field.$dirty && field.$invalid;
    };

    $scope.saveContact = function (contact) {
        $scope.sendBtnDisable = true;

        if ($scope.contactForm.$invalid) {
            $scope.sendBtnDisable = false;
            return
        }

        var recaptcha = grecaptcha.getResponse(window.recaptchaContact);
        if (recaptcha == '') {
            $scope.contactCaptchaValid = false;
            $scope.sendBtnDisable = false;
            return
        }

        var recaptchaData = {
            response: recaptcha
        };

        $http.post('/main/rest/recaptcha', recaptchaData).then(function (resp) {
            if (resp.success == false) {
                console.log(resp);
                $scope.contactCaptchaValid = false;
                $scope.sendBtnDisable = false;
                return
            }

            $scope.contactCaptchaValid = true;

            $http.post('/main/rest/callback/add', contact).then(function (resp) {
                if (resp.data.success == false) {
                    console.log(resp);
                    $scope.sendBtnDisable = false;
                    return
                }

                alert('Сообщение отправлено');
                $scope.contact = {};
                $scope.contactForm.$setPristine();
                grecaptcha.reset(window.recaptchaContact);
                $scope.sendBtnDisable = false;
            })
        });
    };


    //$('form[name=contactForm] [type=tel]').on('change blur', function () {
    //    $scope.contact.phone = $(this).val();
    //    $scope.contactForm.phone.$dirty = true
    //});
});
