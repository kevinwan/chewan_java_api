'use strict';

gpjApp.controller('feedbackInfoModalController', function ($scope, $rootScope, $modalInstance, chewanService, $window) {

    /**
     * Get feedback info
     */
    $rootScope.loadingPromise = chewanService.getFeedbackInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            //alert(JSON.stringify(result.data));
            $scope.feedback = result.data;
        }
    });

    /**
     * Update remarks info
     */
    $scope.updateRemarks = function () {
        if(!$scope.feedback.remarks)
            return alert('请输入客服备注');

        $rootScope.loadingPromise = chewanService.alterFeedbackInfo($scope.feedback.remarks).success(function (result) {
            if (result && result.result == 0) {
                alert('处理反馈信息成功');
                return $modalInstance.dismiss('refresh');
            }
            alert('处理反馈信息失败');
        });
    };

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function(photo){
        //alert(photo);
        $window.open(photo,'_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});