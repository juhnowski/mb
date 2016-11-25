app = angular.module('app', ['layout'])


app.controller('BalanceCtrl', function ($scope, $resource) {
    var resRepay = $resource('/main/rest/listsums/getData')

    resRepay.get({}, function (data) {
        $scope.balanceInfo = data.data
    })
});
