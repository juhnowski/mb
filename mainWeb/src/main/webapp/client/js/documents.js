app = angular.module('app', ['layout']);

app.controller('DocumentsCtrl', function ($scope, $resource, $timeout) {

    var documentList = $resource('/main/rest/documents/list');
    documentList.save(null, function (data) {
        $scope.documents = data.data;

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
