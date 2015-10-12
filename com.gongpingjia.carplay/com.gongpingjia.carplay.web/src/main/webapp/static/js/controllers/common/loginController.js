'use strict';

/**
 * LoginController
 *
 * @constructor
 */
gpjApp.controller('loginController', ['$scope', '$window', 'userService', 'authService', 'md5', function ($scope, $window, userService, authService, md5) {

    $scope.login = function (user) {
        if (user && user.username && user.password) {
            userService.logIn(user.username, md5.createHash(user.password)).success(function (resp) {
                if (resp.status === "success") {
                    authService.setUser({username: 'gpj_admin', remember: user.remember});
                    $window.location.href = '/'
                } else
                    alert(resp.msg ? resp.msg : '登录失败');
            }).error(function (status, data) {
                alert('网络错误');
            });
        }
    };
}]);
 
	 