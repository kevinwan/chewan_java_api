'use strict';

/**
 * userStatisticsController
 *
 * @constructor
 */
gpjApp.controller('userStatisticsController', ['$scope', '$rootScope', 'operationService',
    function ($scope, $rootScope, operationService) {

        $scope.userAmountChartType = "bar";
        $scope.userLocateChartType = "pie";

        /**
         * Initialize component status
         */
        $scope.newUserConfig = {
            labels: true,
            title: "新增用户数",
            legend: {
                display: true,
                position: 'left'
            },
            innerRadius: 0
        };

        /**
         * Initialize component status
         */
        $scope.userLocateConfig = {
            labels: true,
            title: "用户省份分布",
            legend: {
                display: true,
                position: 'left'
            },
            innerRadius: 0
        };

        /**
         * Get user statistics from backend
         */
        $scope.getUserAmountStatistics = function (type) {
            $rootScope.loadingPromise = operationService.getNewUserStatistics(type).success(function (result) {
                if (result.result === 0) {
                    $scope.newUserData = {
                        series: [''],
                        data: result.data
                    };
                } else {
                    alert('未能成功获取新增车主数');
                }
            });
        };

        /**
         * Get user locate statistics from backend
         */
        $scope.getUserLocateStatistics = function () {
            $rootScope.loadingPromise = operationService.getUserLocateStatistics().success(function (result) {
                if (result.result === 0) {
                    $scope.userLocateData = {
                        series: ['用户省份分布'],
                        data: result.data
                    };
                } else {
                    alert('未能成功获取用户省份分布信息');
                }
            });
        };

        $scope.getUserAmountStatistics($scope.type);
        $scope.getUserLocateStatistics();
    }]);