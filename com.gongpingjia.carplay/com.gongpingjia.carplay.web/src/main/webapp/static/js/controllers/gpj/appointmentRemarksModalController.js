'use strict';

gpjApp.controller('appointmentRemarksModalController', function ($scope, $rootScope, $modalInstance, gpjService) {

    $scope.record = gpjService.getAppointment();
    //alert(JSON.stringify($scope.record));
    $scope.record.process_status = gpjService.getStatusField($scope.record.process_status);

    /**
     * Update
     */
    $scope.update = function () {
        //alert(JSON.stringify($scope.record));

        $rootScope.loadingPromise = gpjService.postPromoAppointmentNote({
            book_id: $scope.record.book_id,
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
        $scope.record.process_status = gpjService.getStatusName($scope.record.process_status);
        $modalInstance.dismiss('close');
    };
});