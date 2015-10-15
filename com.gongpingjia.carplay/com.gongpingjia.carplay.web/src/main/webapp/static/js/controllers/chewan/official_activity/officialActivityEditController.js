'use strict';


gpjApp.controller('officialActivityEditController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonService',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window, commonService) {

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
                        $scope.activity.onFlag = $scope.activity.onFlag ? 'true' : 'false';

                        $scope.photoUrl = $scope.activity.cover.url;
                        //$scope.activity.start
                        document.getElementById("startDate").value = commonService.transferLongToDateString($scope.activity.start);
                        document.getElementById("startTime").value = commonService.transferLongToTimeString($scope.activity.start);
                        if ($scope.activity.end != undefined && $scope.activity.end != null && $scope.activity.end !== "") {
                            document.getElementById("endDate").value = commonService.transferLongToDateString($scope.activity.end);
                            document.getElementById("endTime").value = commonService.transferLongToTimeString($scope.activity.end);
                        }
                    }
                });
            }
        };


        function checkTime() {
            var startDate = document.getElementById("startDate").value;
            var startTime = document.getElementById("startTime").value;
            var endDate = document.getElementById("endDate").value;
            var endTime = document.getElementById("endTime").value;
            if (startDate == undefined || startDate == null || startDate == "") {
                $window.alert("请选择开始时间");
                return false;
            }
            if (startTime == undefined || startTime == null || startTime == "") {
                $window.alert("请选择开始时间");
                return false;
            }
            if (endDate != undefined && endDate != null && endDate != "") {
                if (endTime == undefined || endTime == null || endTime == "") {
                    $window.alert("请选择结束时间");
                    return false;
                }
            }
            if (endTime != undefined && endTime != null && endTime != "") {
                if (endDate == undefined || endDate == null || endDate == "") {
                    $window.alert("请选择结束时间");
                    return false;
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
            return true;
        }


        $scope.updateOfficialActivity = function () {
            //
            if (checkTime()) {
                $rootScope.loadingPromise = officialActivityService.updateOfficialActivity($scope.activity.officialActivityId, $scope.activity).success(function (result) {
                    if (result.result == 0) {
                        $window.alert("更新成功");
                    } else {
                        $window.alert("更新失败");
                    }
                });
            }
        }

        /**
         * register
         */
        $scope.register = function () {

            if (checkTime()) {
                officialActivityService.saveOfficialActivity($scope.activity).success(function (data) {
                    if (data.result == 0) {
                        $window.alert("创建成功");
                        $location.path('/officialActivity/list');
                    } else {
                        $window.alert("创建失败 请检查参数");
                    }
                });
            }
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

        $scope.checkOnItemStatus = function (onFlag, end) {
            if (onFlag == false) {
                //未上架
                return 0;
            } else {
                //上架中
                if (end == undefined || end == null || end == "") {
                    return 1;
                } else {
                    var nowTime = new Date().getTime();
                    //当前时间大于 活动 截止时间 活动处于下架状态
                    if (nowTime > end) {
                        return 2;
                    } else {
                        //活动没有到截止时间 处于 上架中
                        return 1;
                    }
                }
            }
        };

        function validateAll(){

        };
        $scope.initData();
    }
]);