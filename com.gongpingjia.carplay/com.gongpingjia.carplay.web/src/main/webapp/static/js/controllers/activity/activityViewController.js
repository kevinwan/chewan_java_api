'use strict';


gpjApp.controller('activityViewController', ['$scope', '$rootScope', '$location', 'activityService', 'moment', '$window', 'commonService',
    function ($scope, $rootScope, $location, activityService, moment, $window, commonService) {


        /**
         * Cancel button click handler
         */
        $scope.close = function () {
            $location.path('/officialActivity/list');
        };

        $scope.initData = function () {
            var activityId = activityService.getActivityId();

            if(activityId === ''){
                return;
            }

            $rootScope.loadingPromise = activityService.viewActivity(activityId).success(function (result) {
                //获取数据成功
                if (result.result === 0) {
                    //初始化时间
                    $scope.activity = result.data;
                    $scope.activity.transfer = $scope.activity.transfer ? 'true' : 'false';
                } else {
                    $window.alert("获取数据失败");
                }
            });
        }

        $scope.initData();
    }
]);