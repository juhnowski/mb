var app = angular.module('app', [
    'ngSanitize',
    'layout',
    'lk.directives'
]);

app.controller('AddCreditCtrl', function ($http, $scope) {
    $http.get('/main/rest/addcredit/info').then(function (resp) {
        var data = resp.data;
        $scope.allowed = data.allowed;
        $scope.data = data;
        $scope.slidersConfig = {
            sumMin: data.creditSumMin,
            sumMax: data.creditSumMax,
            creditDaysMin: data.creditDaysMin,
            creditDaysMax: data.creditDaysMax
        };

        $scope.local = {
            creditSum: data.creditSum,
            creditDays: data.creditDays
        };

        $scope.$watch('local', function(local) {
            var totalDays = local.creditDays + data.additionalDayPayment;
            var stake = calculateStakeUnknown(local.creditSum, local.creditDays, data.stakeMin, data.stakeMax);
            var diskont = stake * local.creditSum * totalDays;
            diskont = Math.floor(diskont);

            var d = new Date();
            d.setDate(d.getDate() + local.creditDays);

            local.procent_zaim = sep(stakeClean(stake * 100));
            local.total_zaim = sep(diskont + local.creditSum);
            local.date_zaim_html = ('0' + d.getDate()).slice(-2) + ' / ' + ('0' + (d.getMonth() + 1)).slice(-2) + ' / ' + d.getFullYear();
        }, true);
    });

    $scope.getCredit = function getCredit() {
        $http.post('/main/rest/addcredit', {
            creditSum: $scope.local.creditSum,
            creditDays: $scope.local.creditDays,
            behavior:  {}
        }).then(function (resp) {
            window.location.replace('/main/client/congr.shtml');
        });
    }
});

/**
 * Убирает 0 (ноль) в периоде.
 *
 * Например при sum=1000 и days=6 ставка составит 3.5 000 000 000 000 004, процедура преобразует ставку в 3.5
 * @param float Ставка из расчета
 * @returns {number} Ставка после очистки 0 в периоде
 */
function stakeClean(stake) {
    return (stake * 100).toFixed(0) / 100;
}
