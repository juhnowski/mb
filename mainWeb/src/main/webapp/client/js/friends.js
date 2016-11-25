app = angular.module('app', ['layout']);


app.controller('FriendsCtrl', function ($scope, $resource, $timeout) {
    $scope.infoPrefix = 'friend-info-';
    var resFriends = $resource('/main/rest/friends/:id', null, {
        update: {
            method: 'put'
        }
    });


    function updateListOfFriends() {
        resFriends.get({}, function (data) {
            $scope.friends = data.data.friends;

            $timeout(makeInterface, 0)
        });
    }
    updateListOfFriends();

    $scope.updateFriend = function (isValid, friend) {
        if (!isValid) {
            return
        }

        resFriends.update(friend, function (data) {
            if (data.success == false) {
                console.log(data);
                return
            }

            $scope.fold($scope.infoPrefix + friend.id)
        })
    };

    $scope.removeFriend = function (friend) {
        if (!confirm('Вы действительно хотите удалить друга?')) {
            return;
        }

        resFriends.delete({id:friend.id}, function (data) {
            if (data.success == false) {
                console.log(data);
                return;
            }

            $scope.friends.map(function (elem, i) {
                if (elem.id == friend.id) {
                    $scope.friends.splice(i, 1)
                }
            })
        })
    };

    $scope.showAddFriend = function () {
        $scope.newFriend = {};
        $scope.newFriendForm.$setPristine();

        $('#new-friend').toggle('fold')
    };

    $scope.addFriend = function (newFriend) {
        if (!$scope.newFriendForm.$valid) {
            return
        }

        resFriends.save(newFriend, function (data) {
            if (data.success == false) {
                console.log(data);
                return
            }

            updateListOfFriends();
            $('#new-friend').toggle('fold')
            $timeout(makeInterface, 0)
        })
    };
});
