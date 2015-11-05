'use strict';

/**
 * InsuranceController
 *
 * @constructor
 */
gpjApp.controller('mainController', ['$scope', '$rootScope', '$window', 'authService', '$notification', 'userService','$modal',
    function ($scope, $rootScope, $window, authService, $notification, userService,$modal) {

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

        /**
         * 修改密码
         */
        $scope.changePsw = function(){
            var modalInstance = $modal.open({
                templateUrl: 'views/user/change_password.html',
                controller: 'changePasswordController'
            });
        };

        //$notification.notify('images/avatars/avatar.png', 'title', 'content');
    }]);