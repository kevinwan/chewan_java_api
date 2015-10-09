'use strict';

gpjApp.controller('simpleInfoModalController', ['$scope', '$modalInstance', 'simpleInfoModalService',
    function ($scope, $modalInstance, simpleInfoModalService) {

        $scope.info = simpleInfoModalService.getInfo();

        $scope.ok = function () {
            $modalInstance.close(true);
        };

        $scope.cancel = function () {
            $modalInstance.close(false);
        };
    }]);