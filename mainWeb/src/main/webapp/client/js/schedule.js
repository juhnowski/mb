app = angular.module('app', ['layout'])


app.controller('ScheduleCtrl', function ($scope, $resource, $timeout) {
    var resListSchedule = $resource('/main/rest/listpayment/getLstSched')

    resListSchedule.save({token: ''}, function (data) {
        $scope.payments = data.data

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
