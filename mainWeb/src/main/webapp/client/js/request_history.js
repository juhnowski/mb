app = angular.module('app', ['layout'])


app.controller('HistoryCtrl', function ($scope, $resource, $timeout) {
    var resListPayment = $resource('/main/rest/listpayment/getHistory')
    resListPayment.save({token: ''}, function (resp) {
        $scope.credits = resp.data.credit;
        $scope.creditRequests = resp.data.creditReq

        $timeout(function () {
            bindElem()
        }, 1)
    })
});

var bindElem = function() {
    $('.history-loan__head').on('click', function (e) {
        var link = $(this)

        if (link.hasClass('history-loan__head--active')) {
            link.removeClass('history-loan__head--active')
        } else {
            link.addClass('history-loan__head--active')
        }
    })
}
