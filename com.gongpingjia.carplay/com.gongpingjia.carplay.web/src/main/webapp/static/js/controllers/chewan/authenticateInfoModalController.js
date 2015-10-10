'use strict';

gpjApp.controller('authenticateInfoModalController', function ($scope, $rootScope, $modalInstance, authenticationService, moment, $window) {

    var DEFAULT_REMARKS = '不是有效的行驶证图片';

    /**
     * Get feedback info
     */
    $rootScope.loadingPromise = authenticationService.getApplicationInfo().success(function (result) {
        if (result && result.result == 0 && result.data) {
            $scope.application = result.data;
        }
    });

    /**
     * Check if mandatory fields are filled in
     */
    var passMandatoryCheck = function () {
        //alert(JSON.stringify($scope.license));
        return ($scope.license && $scope.license.name && $scope.license.plate && $scope.license.vehicleType && $scope.license.model
        && $scope.license.vehicleNumber && $scope.license.engineNumber && $scope.license.registerTime && $scope.license.issueTime);
    };


    /**
     * Accept authentication application
     */
    $scope.accept = function () {
        //alert('accept');
        if (!passMandatoryCheck())
            alert('请将行驶证的信息录入系统后再次点击同意按钮');
        else {
            $scope.license.registerTime = moment($scope.license.registerTime).valueOf();
            $scope.license.issueTime = moment($scope.license.issueTime).valueOf();
            $rootScope.loadingPromise = chewanService.processApplication($scope.application.id, 1, $scope.license, '').success(function (result) {
                if (result && result.result == 0) {
                    alert('成功完成车主认证');
                    $modalInstance.close('refresh');
                } else {
                    alert(result.errmsg);
                }
            });
        }
    };

    /**
     * Decline authentication application
     */
    $scope.decline = function () {
        $scope.isDeclined = true;

        if (!($scope.remarks))
            alert('请在底部填写拒绝理由');
        else {
            var remarks = $scope.remarks ? $scope.remarks : DEFAULT_REMARKS;
            $rootScope.loadingPromise = chewanService.processApplication($scope.application.id, 0, undefined, remarks).success(function (result) {
                if (result && result.result == 0) {
                    alert('已拒绝审核');
                    $modalInstance.close('refresh');
                } else {
                    alert(result.errmsg);
                }
            });
        }
    };

    /**
     * Update license info
     */
    $scope.update = function () {
        alert('该功能未开发完成');
        $modalInstance.dismiss('close');
    };

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function(){
        $window.open($scope.application.license,'_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});