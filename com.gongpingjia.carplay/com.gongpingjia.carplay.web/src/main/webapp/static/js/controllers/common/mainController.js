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
            userService.logOut();
            $window.location.href = 'login.html'
        };

        //$notification.notify('images/avatars/avatar.png', 'title', 'content');
    }]);