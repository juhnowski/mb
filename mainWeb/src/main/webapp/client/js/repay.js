app = angular.module('app', ['layout']);


app.controller('RepayCtrl', function ($scope, $resource, $timeout, $location) {
    $scope.tab = {1:false,2:false};
    $scope.toggleTab = function(num) {
        $scope.tab[num] = !$scope.tab[num];
        if ($scope.tab[num]) {
            for (var key in $scope.tab){
                if (key != num) {
                    $scope.tab[key] = false;
                }
            }
        }
    }
    var resRepayData = $resource('/main/rest/anypayment/getRepayData');
    var resRepay = $resource('/main/rest/payment/:provider/create');

    $scope.isRangeOk = true;

    function addField(form, name, value) {
        var field = document.createElement('INPUT');
        field.name = name;
        field.type = 'hidden';
        field.value = value;
        form.appendChild(field);
    }

    resRepayData.get({}, function (data) {
        $scope.repay = data;
        $timeout(makeInterface, 0)
    });

    $scope.pay = function (type, sum) {
        if (!$scope.isRangeOk) {
            return
        }

        var data = {
            paymentSum: sum,
            accountNumber: $scope.repay.credit_creditAccount
        };

        resRepay.save({'provider': type}, data, function (response) {
            console.log(response)
        })
    };

    $scope.winpay = function (e) {
        if (!$scope.isRangeOk) {
            return
        }

        var baseUrl = $location.protocol() + '://' + $location.host() + ($location.port() == 80 ? '' : ':'+$location.port());

        var data = {
            sum: $scope.repay.sumBack,
            successUrl: baseUrl + '/main/client/winpay-complete-success.shtml',
            failureUrl: baseUrl + '/main/client/winpay-complete-fail.shtml'
        };

        resRepay.save({'provider': 'winpay'}, data, function (response) {
            console.log(response);

            var fields = {
                'data': response.data,
                'sign': response.sign
            }
            for(var name in fields) {
                addField(e.target, name, fields[name]);
            }
            e.target.action = response.paymentUrl;
            e.target.submit();
        })
    };

    $scope.yandex = function (e) {
        if (!$scope.isRangeOk) {
            return
        }

        var data = {
            sum: $scope.repay.sumBack,
        };

        resRepay.save({'provider': 'yandex'}, data, function (response) {
            console.log(response);
            var baseUrl = $location.protocol() + '://' + $location.host() + ($location.port() == 80 ? '' : ':'+$location.port());

            var fields = {
                'shopId': response.shopId,
                'scid': response.scid,
                'sum': parseFloat($scope.repay.sumBack).toFixed(2),
                'customerNumber': response.customerNumber,
                'orderNumber': response.orderNumber,
                'paymentType': 'PC',
                'shopSuccessURL': baseUrl + '/main/client/yandex-complete-success.shtml',
                'shopFailURL': baseUrl + '/main/client/yandex-complete-fail.shtml'

            }
            for(var name in fields) {
                addField(e.target, name, fields[name]);
            }
            e.target.action = response.paymentUrl;
            e.target.submit();
        })
    };
      $scope.checkMin = function(value, min) {
        if (value >= min) {
            $scope.isRangeOk = true
        } else {
            $scope.isRangeOk = false
        }
    }

});