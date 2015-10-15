'use strict';

/**
 * promoScanController
 *
 * @constructor
 */
gpjApp.controller('promoRankController', ['$scope', '$rootScope', 'gpjService', 'moment',
    function ($scope, $rootScope, gpjService, moment) {

        $scope.criteria = {
            type: 'scan',
            city: '北京',
            startDate: moment().startOf('month').format('YYYY-MM-DD'),
            endDate: moment().format('YYYY-MM-DD')
        };

        $scope.dateOptions = {
            changeYear: false,
            changeMonth: false,
            yearRange: '1900:-0'
        };

        /**
         * Initialize component status
         */
        $scope.rankConfig = {
            labels: true,
            title: "",
            legend: {
                display: true,
                position: 'left'
            },
            innerRadius: 0
        };

        /**
         * Get rank statistics from backend
         */
        $scope.getRankStatistics = function () {
            //alert(JSON.stringify($scope.criteria));

            $rootScope.loadingPromise = gpjService.getPromoRank({
                type: $scope.criteria.type,
                city: $scope.criteria.city ? $scope.criteria.city : '',
                start_time: $scope.criteria.startDate ? $scope.criteria.startDate : '',
                end_time: $scope.criteria.endDate ? $scope.criteria.endDate : '',
                order_by: 'code',
                asc: 1

            }).success(function (res) {
                //alert(JSON.stringify(res.data));
                if (res.status !== 'success')
                    return alert('未能成功获取数据');

                $scope.rankData = {
                    series: ['完成量'],
                    data: res.data.map(function (v, i, a) {
                        return {
                            x: v.code,
                            y: [v.count],
                            tooltip: '姓名：' + v.name + '<br>完成量：' + v.count + '<br>联系方式：' + v.phone
                        };
                    })
                };

                console.dir($scope.rankData);
            });
        };

        $scope.getRankStatistics();
    }]);