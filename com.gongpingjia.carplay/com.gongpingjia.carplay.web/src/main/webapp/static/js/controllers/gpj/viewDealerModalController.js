'use strict';

gpjApp.controller('viewDealerModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    var editMode = 0;
    $scope.operation = '查看';
    $scope.btnUpdateLabel = '修改';
    $scope.isDisabled = true;

    $scope.dealer = {
        dealer_id: gpjService.getDealer().dealer_id,
        dealer_name: gpjService.getDealer().dealer_name,
        dealer_city: gpjService.getDealer().dealer_city,
        dealer_phone: gpjService.getDealer().dealer_phone
    };

    /**
     * Update dealer info
     *
     * @param dealer
     */
    $scope.update = function (dealer) {
        $scope.operation = '修改';
        $scope.isDisabled = false;

        if (editMode === 0) {
            editMode = 1;
            return;
        }
        if (!(dealer.dealer_name && dealer.dealer_city && dealer.dealer_phone))
            return alert('请填写全部信息');

        $rootScope.loadingPromise = gpjService.updateDealer($scope.dealer).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                alert('成功修改车商: ' + dealer.dealer_name);
                return $modalInstance.close('refresh');
            } else
                alert(resp.msg ? resp.msg : '未能成功修改车商信息');
        }, function (err) {
            alert('未能成功修改车商信息');
        });
    };

    /**
     * Delete dealer info
     *
     * @param dealer
     */
    $scope.delete = function () {
        if (confirm("删除本条记录？")) {
            $rootScope.loadingPromise = gpjService.deleteDealer({dealer_id: $scope.dealer.dealer_id}).success(function (resp) {
                if (resp && (resp.status === 'success')) {
                    alert('成功删除车商: ' + $scope.dealer.dealer_name);
                    return $modalInstance.close('refresh');
                } else
                    alert(resp.msg ? resp.msg : '未能成功删除车商信息');
            }, function (err) {
                alert('未能成功删除车商信息');
            });
        }
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});