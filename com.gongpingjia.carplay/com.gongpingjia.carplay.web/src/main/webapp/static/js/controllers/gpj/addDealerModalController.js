'use strict';

gpjApp.controller('addDealerModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    /**
     * 创建车商
     *
     * @param dealer
     */
    $scope.add = function (dealer) {
        //alert(JSON.stringify(dealer));
        if (!(dealer.name && dealer.city && dealer.phone))
            return alert('请填写全部信息');

        $rootScope.loadingPromise = gpjService.addDealer({
            dealer_name: dealer.name,
            dealer_city: dealer.city,
            dealer_phone: dealer.phone

        }).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                alert('成功创建车商: ' + dealer.name);
                return $modalInstance.close('refresh');
            } else
                alert(resp.msg ? resp.msg : '未能成功创建车商');
        }, function (err) {
            alert('未能成功创建车商');
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});