'use strict';

gpjApp.controller('c2cExchangeModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    $scope.isDisabled = true;
    $scope.exchange = gpjService.getC2CExchange();
    $scope.exchange.process_status = gpjService.getStatusField($scope.exchange.process_status);

    /**
     * 更新客服备注
     *
     * @param dealer
     */
    $scope.updateRemarks = function (remarks) {
        //alert(remarks);

        $rootScope.loadingPromise = gpjService.postTradeCarNote({
            trade_car_id: $scope.exchange.publish_id,
            process_status: $scope.exchange.process_status,
            process_remarks: $scope.exchange.process_remarks

        }).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                alert('成功修改备注');
                return $modalInstance.close('refresh');
            } else
                alert(resp.msg ? resp.msg : '未能成功修改备注');
        }, function (err) {
            alert('未能成功修改备注');
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});