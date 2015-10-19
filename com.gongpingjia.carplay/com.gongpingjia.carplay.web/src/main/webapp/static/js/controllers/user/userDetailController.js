'use strict';

gpjApp.controller('userInfoModalController', function ($scope, $rootScope, $location, $modalInstance, userService, moment, $window) {

    /**
     * Get feedback info
     */
    $rootScope.loadingPromise = userService.getUserInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            $scope.user = result.data;
        }
    });

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function () {
        $window.open($scope.user.photo, '_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $location.path("/user/list");
    };

    $scope.update = function () {
        var result = userService.updateUserInfo($scope.user);
        if (result && result.result == 0 && result.data) {
            alert("更新用户信息成功");
        } else {
            alert("更新用户信息失败");
        }
        $location.path("/user/list");
    }
});