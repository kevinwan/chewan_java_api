'use strict';

gpjApp.controller('noDealerTradeModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    $scope.isDisabled = true;
    $scope.noDealerTrade = gpjService.getNoDealerTrade();
    $scope.noDealerTrade.process_status = gpjService.getStatusField($scope.noDealerTrade.process_status);

    //alert(JSON.stringify($scope.noDealerTrade));

    /**
     * 创建车商
     *
     * @param dealer
     */
    $scope.updateRemarks = function (trade) {
        //alert(JSON.stringify(trade));

        $rootScope.loadingPromise = gpjService.updateRemarksForOpenSell({
            trade_car_id: $scope.noDealerTrade.open_sell_id,
            process_status: $scope.noDealerTrade.process_status,
            process_remarks: $scope.noDealerTrade.process_remarks

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