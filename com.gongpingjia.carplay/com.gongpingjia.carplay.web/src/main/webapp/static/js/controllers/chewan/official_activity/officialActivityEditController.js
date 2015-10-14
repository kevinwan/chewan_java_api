'use strict';

gpjApp.controller('officialActivityEditController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window) {

        /**
         * Cancel button click handler
         */
        $scope.close = function () {
            $location.path('/officialActivity/list');
        };

        $scope.initData = function () {
            var officialActivityId = officialActivityService.getOfficialActivityId();
            if (officialActivityId === '') {
                return;
            } else {
                $rootScope.loadingPromise = officialActivityService.getOfficialActivity(officialActivityId).success(function (result) {
                    //$scope.activity = (result.result === 0 ? result.data : undefined);
                    //if($scope.activity.cover != undefined && $scope.activity.cover != null) {
                    //    $scope.photoUrl = $scope.activity.cover.photoUrl;
                    //}
                    if(result.result === 0) {

                    }
                });
            }
        };

        $scope.updateOfficialActivity = function () {
            $rootScope.loadingPromise = officialActivityService.updateOfficialActivity($scope.activity).success(function (result) {
                if (result.result == 0) {
                    $window.alert("更新成功");
                } else {
                    $window.alert("更新失败");
                }
            });
        }

        /**
         * register
         */
        $scope.register = function () {
            var startTime = $scope.activity.start;
            var endTime = $scope.activity.end;

            officialActivityService.saveOfficialActivity($scope.activity).success(function (data) {
                if (data.result == 0) {
                    $window.alert("创建成功");
                    $location.path('/officialActivity/list');
                }
            });
        }



        $scope.uploadFile = function (data) {
            var formData = new FormData();
            formData.append('attach', data.files[0]);

            $rootScope.loadingPromise = officialActivityService.uploadFile(formData).success(function (result) {
                if (result.result === 0) {
                    var cover = {};
                    cover.id = result.data.photoId;
                    cover.photoUrl = result.data.photoUrl;
                    cover.key = result.data.photoKey;
                    $scope.activity.cover = cover;
                    $scope.photoUrl = cover.photoUrl;
                }
            });
        };

        $scope.initData();
    }
]);