'use strict';

/**
 * InsuranceController
 *
 * @constructor
 */
gpjApp.controller('mainController', ['$scope', '$rootScope', '$window', 'authService', '$notification',
    function ($scope, $rootScope, $window, authService, $notification) {

        $rootScope.loadingPromise = $scope.loadingPromise;

        $scope.userRole = "ADMIN";//authService.getUser().role

        $scope.logout = function () {
            authService.setUser('');
            $window.location.href = '/login.html';
        };

        //$notification.notify('images/avatars/avatar2.png', '新的上门洗车订单', '车牌号12345');
    }]);