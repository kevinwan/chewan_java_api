'use strict';

gpjApp.controller('userInfoModalController', function ($scope, $rootScope, $modalInstance, chewanService, moment, $window) {

    /**
     * Get feedback info
     */
    $rootScope.loadingPromise = chewanService.getUserInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            //alert(JSON.stringify(result.data));
            result.data.isAuthenticated = (result.data.isAuthenticated) ? '已认证' : '未认证';
            $scope.user = result.data;
        }
    });

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function(){
        $window.open($scope.user.photo,'_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});