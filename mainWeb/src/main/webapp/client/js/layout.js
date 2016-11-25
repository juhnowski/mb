app = angular.module('layout', ['ngResource']);


app.factory('Bonus', function ($resource) {
    return $resource('/main/rest/bonus/info')
});


app.directive('fold', function () {
    return {
        restrict: 'A',
        scope: {
            fold: '@'
        },
        link: function (scope, element, attrs) {
            var regExpClass = /^(.*)__(\d*)$/;
            var className = scope.fold.match(regExpClass)[1];
            var currentClassNumber = scope.fold.match(regExpClass)[2];

            element.on('click', function () {
                $('[class*=' + className + ']').map(function (i, elem) {
                    var foldElement = $(elem);
                    foldElement.attr('class').split(/ /).map(function (elem) {
                        if (elem.indexOf(className) != -1) {
                            if (elem.match(regExpClass)[2] != currentClassNumber) {
                                if (foldElement.is(':visible')) {
                                    foldElement.toggle('fold')
                                }
                            } else {
                                foldElement.toggle('fold')
                            }
                        }
                    })
                });
            })
        }
    };
});


app.directive('mask', function () {
    return {
        restrict: 'A',
        scope: {
            mask: '@'
        },
        link: function (scope, element, attrs) {
            $(element).mask(scope.mask);
        }
    };
});


app.directive('validCard', function () {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attr, ctrl) {
            function customValidator(ngModelValue) {
                ctrl.$setValidity('creditCardValidator', validCreditCard(ngModelValue));

                return ngModelValue;
            }

            ctrl.$parsers.push(customValidator);
        }
    };
});


app.controller('MainCtrl', function ($scope, $resource, $http, Bonus) {
    var api = new API();

    var usernameUrl = '/main/rest/usersettings/getUserName';
    var savingsUrl = '/main/rest/listsums/mySum';
    var logoutUrl = '/main/rest/auth/logout';
    var captchaUrl = '/main/rest/recaptcha';
    var callbackUrl = '/main/rest/callback/add';


    Bonus.get({}, function (data) {
        $scope.bonus = (data.data.bonusAmount || 0).toString().replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, "$1 ")
    });

    $http.post(usernameUrl).then(function (resp) {
        $scope.username = resp.data.data.split(' ').slice(0, 2).join(' ')
    });

    $http.get(savingsUrl).then(function (resp) {
        $scope.savings = resp.data.data
    });

    $scope.ai = function () {
        api.aiData().done(function (data) {
            $scope.aiData = data
            $scope.$apply()
            var elements = $('[data-ai]');

            elements.map(function (i, elem) {
                elem = $(elem);
                var parameters = eval("(" + elem.data().ai + ")");

                if (data[parameters.value]) {
                    elem.addClass(parameters.yes);
                } else {
                    elem.addClass(parameters.no).prop("disabled", true);
                    elem.find(parameters.for).addClass(parameters.no).prop("disabled", true);
                }
            });
        });
    };
    $scope.ai();

    $scope.logout = function () {
        $http.post(logoutUrl).then(function () {
            window.location.replace('/main/index.shtml');
        })
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

        $http.post(captchaUrl, recaptchaData).then(function (resp) {
            if (resp.success == false) {
                console.log(resp);
                $scope.callbackCaptchaValid = false;
                $scope.callbackBtnDisable = false;
                return
            }

            $scope.callbackCaptchaValid = true;

            $http.post(callbackUrl, callback).then(function (resp) {
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

    $scope.fold = function (id) {
        $('#' + id).toggle('fold')
    };

    $scope.isInvalid = function (field) {
        return field.$dirty && field.$invalid;
    };

    $scope.toggleFoldRadio = function (mainId, slaveIds) {
        if (slaveIds != null) {
            slaveIds.map(function (elem) {
                var slave = $('#' + elem);
                if (slave.is(':visible')) {
                    slave.toggle('fold')
                }
            })
        }

        $('#' + mainId).toggle('fold')
    }

    $('.callback_up [type=tel]').on('change blur', function () {
        $scope.callback.phone = $(this).val();
        $scope.callbackForm.phone.$dirty = true
    });
});


/**
 * Выводит\скрывает сообщение об ошибке
 * @param show true - показать сообщение, false - скрыть сообщение
 * @param message сообщение, если оно равно null, то выводится текст "Ошибка выполнения запроса"
 */
var smsError = function (show, message) {
    var error = $('#smsConfirmError');

    if (show) {
        error.show();
        error.html(message || 'Ошибка выполнения запроса');
    } else {
        error.hide();
        error.html('');
    }
};

var validCreditCard = function (value) {
    if (/[^0-9-\s]+/.test(value)) return false;

    value = value.replace(/\D/g, "");
    if (value.length < 16 || value.length > 18) return false;

    var nCheck = 0, bEven = false;

    for (var n = value.length - 1; n >= 0; n--) {
        var cDigit = value.charAt(n),
            nDigit = parseInt(cDigit, 10);

        if (bEven) {
            if ((nDigit *= 2) > 9) nDigit -= 9;
        }

        nCheck += nDigit;
        bEven = !bEven;
    }

    return (nCheck % 10) == 0;
}

var request = function (url, method, data, success, error) {
    $.ajax({
        url: url,
        type: method,
        contentType: "application/json",
        dataType: "json",
        data: data,
        success: success,
        error: error
    });
}

$(document).ready(function () {
    /**
     * для инпутов, можем вводить только цифры
     */
    $('[only-digits]').on('keydown', function (e) {
            -1 !== $.inArray(e.keyCode, [46, 8, 9, 27, 13, 110, 190]) || /65|67|86|88/.test(e.keyCode) && (!0 === e.ctrlKey || !0 === e.metaKey) || 35 <= e.keyCode && 40 >= e.keyCode || (e.shiftKey || 48 > e.keyCode || 57 < e.keyCode) && (96 > e.keyCode || 105 < e.keyCode) && e.preventDefault()
        }
    );

    $(document).ajaxError(function(event, request, settings) {
        if (request.status === 401) {
            window.location = '/main/login.shtml';
        }
    });
});
