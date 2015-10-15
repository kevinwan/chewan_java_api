'use strict';

gpjApp.controller('dealerDealsModalController', function ($scope, $rootScope, $modal, $modalInstance, DTOptionsBuilder, DTColumnDefBuilder, gpjService) {

    /**
     * Define data table columns
     */
    $scope.dtColumnDefs = [DTColumnDefBuilder.newColumnDef(0).notSortable(),
        DTColumnDefBuilder.newColumnDef(1).notSortable(),
        DTColumnDefBuilder.newColumnDef(2).notSortable(),
        DTColumnDefBuilder.newColumnDef(3).notSortable(),
        DTColumnDefBuilder.newColumnDef(4).notSortable(),
        DTColumnDefBuilder.newColumnDef(5).notSortable(),
        DTColumnDefBuilder.newColumnDef(6).notSortable(),
        DTColumnDefBuilder.newColumnDef(7).notSortable(),
        DTColumnDefBuilder.newColumnDef(8).notSortable(),
        DTColumnDefBuilder.newColumnDef(9).notSortable(),
        DTColumnDefBuilder.newColumnDef(10).notSortable(),
        DTColumnDefBuilder.newColumnDef(11).notSortable(),
        DTColumnDefBuilder.newColumnDef(12).notSortable(),
        DTColumnDefBuilder.newColumnDef(13).notSortable(),
        DTColumnDefBuilder.newColumnDef(14).notSortable(),
        DTColumnDefBuilder.newColumnDef(15).notSortable(),
        DTColumnDefBuilder.newColumnDef(16).notSortable()
    ];

    /**
     * Define data table options
     */
    $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
        .withOption('order', [])
        .withOption('bFilter', false)
        .withOption('bLengthChange', false)
        .withOption('displayLength', 5);

    /**
     * Load data
     */
    $scope.getDealerDeals = function () {
        $rootScope.loadingPromise = gpjService.getDealerDeals({
            dealer_id: gpjService.getDealer().dealer_id

        }).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                $scope.deals = resp.data;
            } else
                alert(resp.msg ? resp.msg : '未能成功获取车商拍车记录');
        }, function (err) {
            alert('获取车商拍车记录失败');
        });
    };

    $scope.getDealerDeals();

    /**
     * View remarks
     *
     * @param deal
     */
    $scope.viewRemarks = function (deal) {
        //alert(JSON.stringify(item));

        gpjService.setDeal(deal);

        var modalInstance = $modal.open({
            templateUrl: 'views/gpj/deal_remarks_modal.html',
            controller: 'dealRemarksModalController'
        });

        return modalInstance.result.then(function (reply) {
            $scope.getDealerDeals();
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});