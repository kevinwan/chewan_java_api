'use strict';

/**
 * LoginController
 *
 * @constructor
 */
gpjApp.controller('loginController', ['$scope', '$window', 'userService', 'authService', 'md5', function ($scope, $window, userService, authService, md5) {

    $scope.login = function (user) {
        if (user && user.phone && user.password) {
            userService.logIn(user.phone, md5.createHash(user.password)).success(function (resp) {
                if (resp.result === 0) {
                    authService.setUser(resp.data);
                    $window.location.href = 'v2/'
                } else
                    alert(resp.msg ? resp.msg : '登录失败');
            }).error(function (status, data) {
                alert('网络错误');
            });
        }
    };
}]);
 
	 