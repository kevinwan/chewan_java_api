'use strict';

/**
 * InsuranceController
 *
 * @constructor
 */
gpjApp.controller('mainController', ['$scope', '$rootScope', '$window', 'authService', '$notification', 'userService',
    function ($scope, $rootScope, $window, authService, $notification, userService) {

        $rootScope.loadingPromise = $scope.loadingPromise;

        if(authService.getUser()){
            $('#bodyControl').removeClass('hidden');
            $('#bodyControl').addClass('block');
        }
        /**
         * Log out
         */
        $scope.logout = function () {
            userService.logOut().success(function (resp) {
                if (resp.status === "success") {
                    authService.setUser('');
                    $window.location.href = '/login.html'
                } else
                    alert(resp.msg ? resp.msg : '注销失败');
            }).error(function (status, data) {
            });
        };

        //$notification.notify('images/avatars/avatar.png', 'title', 'content');
    }]);