'use strict';

gpjApp.controller('activityInfoModalController', function ($scope, $rootScope, $modalInstance, chewanService, moment, $window) {

    /**
     * Get activity info
     */
    $rootScope.loadingPromise = chewanService.getActivityInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            //alert(JSON.stringify(result.data));
            $scope.activity = result.data;
        }
    });

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function(){
        $window.open($scope.activity.cover[0].original_pic,'_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});