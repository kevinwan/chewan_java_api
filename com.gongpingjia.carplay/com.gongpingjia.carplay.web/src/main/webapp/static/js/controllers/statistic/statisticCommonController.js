'use strict';


gpjApp.controller('statisticCommonController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonService', 'statisticService', '$routeParams',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window, commonService, statisticService, $routeParams) {

        $scope.config = {
            title: '统计图',
            tooltips: true,
            labels: false,
            legend: {
                display: true,
                position: 'right'
            }
        };

        $scope.criteria = {
            startTime: moment().subtract(6, 'day').format('YYYY-MM-DD'),
            endTime: moment().format('YYYY-MM-DD')
        };
        $scope.type = $routeParams.type;

        $scope.search = function () {
            if ($scope.criteria.endTime < $scope.criteria.startTime) {
                alert("结束时间必须大于开始时间");
                return;
            }
            $rootScope.loadingPromise = statisticService.getCommonInfo($scope.criteria, $scope.type).success(function (res) {
                if (res.result === 0) {
                    $scope.data = res.data;
                } else {
                    alert("获取数据失败");
                }
            });
        };

        $scope.resetCriteria = function () {
            $scope.criteria = {
                startTime: moment().subtract(6, 'day').format('YYYY-MM-DD'),
                endTime: moment().format('YYYY-MM-DD')
            };
        };

        $scope.resetCriteria();
        $scope.search();


    }
]);
