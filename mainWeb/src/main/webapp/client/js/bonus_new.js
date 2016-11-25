app = angular.module('app', ['layout']);


app.controller('BonusCtrl', function ($scope, $resource, $http, Bonus) {
    $scope.sendBtnDisable = false;

    var resFriends = $resource('/main/rest/bonus/friends');
    var resBonusUse = $resource('/main/rest/bonus/props/');


    Bonus.get({}, function (data) {
        $scope.bonusInfo = data.data
    });

    $scope.phoneRequired = false;
    $http.get('/main/rest/auth/type').then(function (resp) {
        if (resp.data.type == "phone") {
            $scope.phoneRequired = true;
        } else {
            $scope.phoneRequired = false;
        }
    });

    $scope.invite = function (newFriend) {
        $scope.sendBtnDisable = true;
        if (!$scope.friendForm.$valid) {
            $scope.sendBtnDisable = false;
            alert("Форма заполнена не верно");
            return
        }
        resFriends.save(newFriend, function (data) {
            if (data.success == false) {
                alert(data.errorMessage);
                $scope.newFriend = {};
                $scope.friendForm.$setPristine();
                $scope.sendBtnDisable = false;
                return
            }

            if (!$scope.phoneRequired) {
                alert('Письмо с приглашением отправлено на e-mail Вашего друга');
            } else {
                alert('Смс с приглашением отправлено на номер Вашего друга.');
            }
            $scope.bonusInfo.friends.push(newFriend);
            $scope.newFriend = {};
            $scope.friendForm.$setPristine();
            $scope.sendBtnDisable = false;
        })
    };

    $scope.saveBonusUsing = function (isUsing) {
        resBonusUse.save(isUsing.toString(), function (data) {
            console.log(data)
            alert("Настройка сохранена")
        })
    }
});
