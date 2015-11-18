'use strict';


gpjApp.controller('statisticRegisterController', ['$scope', '$rootScope', '$location', 'officialActivityService', 'moment', '$window', 'commonService', 'statisticService', '$routeParams',
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


        $scope.rateConfig = {
            title: '转化率图',
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

        $scope.search = function () {
            if ($scope.criteria.endTime < $scope.criteria.startTime) {
                alert("结束时间必须大于开始时间");
                return;
            }
            $rootScope.loadingPromise = statisticService.getCommonInfo($scope.criteria, 1).success(function (res) {
                if (res.result === 0) {
                    $scope.data = res.data;
                    $scope.rateData = {};
                    $scope.rateData.data = [];
                    $scope.rateData.series = ['转化率'];

                    var dataArr = $scope.data.data;
                    for (var index in dataArr) {
                        var item = {};
                        item.x = dataArr[index].x;
                        var rate = dataArr[index].y[1] == 0 ? 100 : (dataArr[index].y[0] / dataArr[index].y[1]) * 100;
                        item.y = [rate];
                        $scope.rateData.data.push(item);
                    }
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