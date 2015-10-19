'use strict';

gpjApp.controller('userInfoModalController', function ($scope, $rootScope, $location, $modalInstance, userService, moment, $window) {

    /**
     * Get feedback info
     */
    $rootScope.loadingPromise = userService.getUserInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            $scope.user = result.data;
            //
            //var photos = $scope.user.photos;
            //if (photos != undefined) {
            //    //<li class="span3">
            //    //    <a href="#" class="thumbnail">
            //    //    <img src="http://placehold.it/260x180" alt="">
            //    //    </a>
            //    //    </li>
            //    for(var photo in photos){
            //        var li = document.createElement("li");
            //        li.addClass("span3");
            //        var a = document.createElement("a");
            //
            //        var img = document.createElement("img");
            //        img.setAttribute("src", photo);
            //
            //    }
            //
            //}
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