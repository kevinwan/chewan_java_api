'use strict';

gpjApp.controller('viewAppointmentsModalController', function ($scope, $rootScope, $modalInstance, DTOptionsBuilder,
                                                               DTColumnDefBuilder, gpjService, $modal) {

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
        DTColumnDefBuilder.newColumnDef(7).notSortable()];

    /**
     * Define data table options
     */
    $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
        .withOption('order', [])
        .withOption('bFilter', false)
        .withOption('bLengthChange', false)
        .withOption('displayLength', 5);

    /**
     * Get promocar appointments
     */
    $scope.getPromocarAppointments = function () {
        $rootScope.loadingPromise = gpjService.getPromocarAppointments({
            publish_id: gpjService.getDealerPublishInfo().publish_id

        }).success(function (resp) {
            if (resp && (resp.status === 'success')) {
                $scope.appointments = resp.data;
            } else
                alert(resp.msg ? resp.msg : '未能成功获取C端预约信息');
        }, function (err) {
            alert('获取C端预约信息失败');
        });
    };

    $scope.getPromocarAppointments();

    /**
     * View remarks
     *
     * @param item
     */
    $scope.viewRemarks = function (item) {
        //alert(JSON.stringify(item));

        gpjService.setAppointment(item);

        var modalInstance = $modal.open({
            templateUrl: 'views/gpj/appointment_remarks_modal.html',
            controller: 'appointmentRemarksModalController'
        });

        return modalInstance.result.then(function (reply) {
            $scope.getPromocarAppointments();
        });
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});