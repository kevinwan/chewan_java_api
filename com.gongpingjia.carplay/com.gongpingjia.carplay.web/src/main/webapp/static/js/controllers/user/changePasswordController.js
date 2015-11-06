/**
 * Created by 123 on 2015/10/13.
 */
'use strict';

/**
 * Driver/Photo/IDCard authentication controller
 *
 * @constructor
 */
gpjApp.controller('changePasswordController', ['$scope', '$rootScope', '$window', 'userService', '$modalInstance','$location','md5',
    function ($scope, $rootScope, $window, userService, $modalInstance,$location,md5) {

        $scope.resetPassword = function(){
            if($scope.newPsw !== $scope.confirmNewPsw) {
                alert("两次密码不一致,请检查");
                return;
            }

            $rootScope.loadingPromise = userService.changePassword({old: md5.createHash($scope.password), new: md5.createHash($scope.newPsw)}).success(function(result){
                if(result.result === 0) {
                    alert("修改成功");
                    $modalInstance.dismiss('close');
                    $window.location.href = 'login.html';
                }else{
                    alert("修改密码失败" + result.errmsg);
                }
            });
        };

        $scope.close = function () {
            $modalInstance.dismiss('close');
        }
    }]);