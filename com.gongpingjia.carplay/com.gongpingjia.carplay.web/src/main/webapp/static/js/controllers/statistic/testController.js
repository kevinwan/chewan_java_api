'use strict';


gpjApp.controller('testController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonService', 'statisticService',
    function ($scope, $rootScope, $location, officialActivityService, moment, $window, commonService, statisticService) {

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
            startTime: moment().subtract(1, 'month').format('YYYY-MM-DD'),
            endTime: moment().format('YYYY-MM-DD')
        };

        $scope.search = function () {
            $rootScope.loadingPromise = statisticService.getUnRegisterInfo($scope.criteria).success(function (res) {
                if (res.result === 0) {
                    $scope.data = res.data;
                } else {
                    alert("获取数据失败");
                }
            });
        };

        $scope.resetCriteria = function () {
            $scope.criteria = {
                startTime: moment().subtract(7, 'day').format('YYYY-MM-DD'),
                endTime: moment().format('YYYY-MM-DD')
            };
        };

        $scope.resetCriteria();
        $scope.search();


    }
]);
