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
                //增加
                $scope.activity = {};
                $scope.activity.limitType = 0;
                return;
            } else {
                $rootScope.loadingPromise = officialActivityService.getOfficialActivity(officialActivityId).success(function (result) {
                    //$scope.activity = (result.result === 0 ? result.data : undefined);
                    //if($scope.activity.cover != undefined && $scope.activity.cover != null) {
                    //    $scope.photoUrl = $scope.activity.cover.photoUrl;
                    //}
                    if (result.result === 0) {
                        //初始化时间
                        $scope.activity = result.data;
                        $scope.photoUrl = $scope.activity.cover.url;
                        //$scope.activity.start

                    }
                });
            }
        };

        function checkTime() {
            var startDate = document.getElementById("startDate").value;
            var startTime = document.getElementById("startTime").value;
            var endDate = document.getElementById("endDate").value;
            var endTime = document.getElementById("endTime").value;
            if (startDate == undefined || startDate == null) {
                $window.alert("请选择开始时间");
                return;
            }
            if (startTime == undefined || startTime == null) {
                $window.alert("请选择结束时间");
                return;
            }
            if (endDate != undefined && endDate != null) {
                if (endTime == undefined || endTime == null) {
                    $window.alert("请选择结束时间");
                    return;
                }
            }
            var startStr = startDate + " " + startTime;
            var endStr = endDate + " " + endTime;

            var start = new Date(startStr);
            var end = new Date(endStr);
            $scope.activity.start = start.getTime();

            if (endStr !== " ") {
                $scope.activity.end = end.getTime();
            } else {
                $scope.activity.end = null;
            }
        }

        $scope.updateOfficialActivity = function () {
            //
            checkTime();

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

            checkTime();

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


        $scope.changeLimitType = function (data) {
            $scope.activity.limitType = parseInt(data.value);
            $scope.$apply();
        };


        $scope.initData();
    }
]);