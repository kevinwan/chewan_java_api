'use strict';

gpjApp.controller('authenticateInfoModalController', function ($scope, $rootScope, $modalInstance, authenticationService, moment, $window) {

    var DEFAULT_REMARKS = '不是有效的行驶证和驾驶证图片';

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
        var license = $scope.application.authentication.license;
        var licenseCheck = license.name && license.plate && license.vehicleType && license.model
            && license.vehicleNumber && license.engineNumber && license.registerTime && license.issueTime;

        var driver = $scope.application.authentication.driver;
        var driverCheck = driver.name && driver.code && driver.gender && driver.nationality && driver.drivingClass &&
            driver.police && driver.birthday && driver.issueDate && driver.validFrom && driver.validFor && driver.address;

        return licenseCheck && driverCheck;
    };


    /**
     * Accept authentication application
     */
    $scope.accept = function () {
        if (!passMandatoryCheck())
            alert('请将行驶证和驾驶证信息录入系统后再次点击同意按钮');
        else {
            $rootScope.loadingPromise = authenticationService.processApplication(true, $scope.remarks, $scope.application).success(function (result) {
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
            $rootScope.loadingPromise = authenticationService.processApplication(false, $scope.remarks, $scope.application).success(function (result) {
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
        if (!passMandatoryCheck())
            alert('请将行驶证和驾驶证信息录入系统后再次点击更新按钮');
        else {
            $rootScope.loadingPromise = authenticationService.updateUserLicense($scope.application.applyUserId, $scope.application.authentication)
                .success(function (result) {
                    if (result && result.result == 0) {
                        alert('信息更新成功');
                        $modalInstance.close('refresh');
                    } else {
                        alert(result.errmsg);
                    }
                });
        }
    };

    /**
     * Browse a photo
     *
     * @param photo
     */
    $scope.browsePhoto = function () {
        $window.open($scope.application.license, '_blank');
    };

    /**
     * Cancel button click handler
     */
    $scope.close = function () {
        $modalInstance.dismiss('close');
    };
});