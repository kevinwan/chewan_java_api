'use strict';

gpjApp.controller('clientPublishRemarksModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    $scope.record = gpjService.getClientPublishInfo();
    //alert(JSON.stringify($scope.record));
    $scope.record.process_status = gpjService.getStatusField($scope.record.process_status);

    /**
     * Update
     */
    $scope.update = function () {
        //alert(JSON.stringify($scope.record));

        $rootScope.loadingPromise = gpjService.postTradeCarNote({
            trade_car_id: $scope.record.open_sell_id,
            process_status: $scope.record.process_status,
            process_remarks: $scope.record.process_remarks

        }).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                alert('成功进行客服备注');
                return $modalInstance.close('refresh');
            } else
                alert(resp.msg ? resp.msg : '未能成功进行客服备注');
        }, function (err) {
            alert('未能成功进行客服备注');
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});