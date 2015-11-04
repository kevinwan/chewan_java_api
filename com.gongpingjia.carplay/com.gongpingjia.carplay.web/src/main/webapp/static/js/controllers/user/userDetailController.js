'use strict';

gpjApp.controller('userDetailController', function ($scope, $rootScope, $location, userService, moment, $window,$routeParams) {

    /**
     * Get feedback info
     */
    $scope.viewUser = function () {
        var  userId = $routeParams.id;
        $rootScope.loadingPromise = userService.getUserInfo(userId).success(function (result) {
            if (result && result.result == 0 && result.data) {
                $scope.user = result.data;
                //$scope.user.album = [];
                //for(var index = 0;index < 10;index ++) {
                //    var temp_photo = {};
                //    temp_photo.url = "http://pic9.nipic.com/20100813/2029588_133424069784_2.jpg";
                //    temp_photo.id = index;
                //    $scope.user.album.push(temp_photo);
                //}
            }else{
                $window.alert("获取用户失败:" + result.errmsg);
            }
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $location.path("/user/list");
    };

    $scope.update = function () {

        $rootScope.loadingPromise = userService.updateUserInfo($scope.user).success(function(result){
            if (result && result.result == 0) {
                alert("保存用户信息成功");
                $location.path("/user/list");
            } else {
                alert("保存用户信息失败");
            }
        });
    };

    $scope.removePhoto = function(photoId) {
        if($window.confirm("确定删除该照片吗?")){
            for(var index in $scope.user.album) {
                if($scope.user.album[index].id === photoId) {
                    $scope.user.album.splice(index, 1);
                }
            }
        }
    };

    $scope.viewUser();
});