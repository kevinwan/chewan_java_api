'use strict';

gpjApp.controller('dealerTradeModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    $scope.isDisabled = true;
    $scope.dealerTrade = gpjService.getDealerTrade();
    $scope.dealerTrade.process_status = gpjService.getStatusField($scope.dealerTrade.process_status);

    /**
     * 创建车商
     *
     * @param dealer
     */
    $scope.updateRemarks = function (remarks) {
        //alert(remarks);

        $rootScope.loadingPromise = gpjService.updateRemarksForOpenSell({
            trade_car_id: $scope.dealerTrade.open_sell_id,
            process_status: $scope.dealerTrade.process_status,
            process_remarks: $scope.dealerTrade.process_remarks

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