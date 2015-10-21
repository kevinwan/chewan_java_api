'use strict';

/**
 * Activity controller
 *
 * @constructor
 */
gpjApp.controller('activityController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'activityService', 'commonService', '$location','$window',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, activityService, commonService, $location, $window,moment) {

        /**
         * Get province and city info
         */
        commonService.getCities().success(function (data) {
            $scope.provinceOptions = data;
        });

        /**
         * Trigger by province change
         */
        $scope.changeProvince = function (province) {
            $scope.criteria.city = '';
            $scope.cityOptions = [];

            //找出 province 对应的 cityOptions
            for (var index in $scope.provinceOptions) {
                if ($scope.provinceOptions[index].province === province) {
                    $scope.cityOptions = $scope.provinceOptions[index].cities;
                    //$scope.$apply();
                    break;
                }
            }
        };


        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable(),
            DTColumnDefBuilder.newColumnDef(1),
            DTColumnDefBuilder.newColumnDef(2),
            DTColumnDefBuilder.newColumnDef(3),
            DTColumnDefBuilder.newColumnDef(4),
            DTColumnDefBuilder.newColumnDef(5),
            DTColumnDefBuilder.newColumnDef(6),
            DTColumnDefBuilder.newColumnDef(7),
            DTColumnDefBuilder.newColumnDef(8),
            DTColumnDefBuilder.newColumnDef(9).notVisible().notSortable()
        ];

        /**
         * Data table row click handler
         */
        var rowClickHandler = function (info) {
        };

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap().withOption('rowCallback',
            function (nRow, aData, iDisplayIndex, iDisplayIndexFull) {
                $('td', nRow).unbind('click');
                $('td', nRow).bind('click', function () {
                    $scope.$apply(function () {
                        rowClickHandler(aData);
                    });
                });
                return nRow;
            }).withOption('order', [0, 'desc']);

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.criteria = {
                phone: '',
                province: '',
                city: '',
                type: '-1',
                pay: '-1',
                transfer: '-1',
                fromDate: moment().format('YYYY-MM-DD'),
                toDate: moment().format('YYYY-MM-DD'),
                fromTime: '',
                toTime: ''
            };
        };


        /**
         * Search activities based on criteria
         */
        $scope.searchActivities = function (criteria) {
            var startDate = $scope.criteria.fromDate;
            var endDate = $scope.criteria.toDate;
            if (startDate !== "") {
                criteria.fromTime = new Date(startDate).getTime();
            } else {
                criteria.fromTime = "";
            }
            if (endDate !== "") {
                criteria.toTime = new Date(endDate).getTime();
            } else {
                criteria.toTime = "";
            }

            $rootScope.loadingPromise = activityService.getActivityList(criteria).success(function (result) {
                if(result.result === 0) {
                    if(result.data === "[]"){
                        $scope.activities = null;
                    }else{
                        $scope.activities = result.data;
                    }
                }
            });
        };

        $scope.updateActivity = function (activityId) {
            activityService.setActivityId(activityId);
            $location.path("/activity/update/"+activityId);
        };

        $scope.viewActivity = function (activityId) {
            activityService.setActivityId(activityId);
            $location.path("/activity/view/"+activityId);
        };


        $scope.initData = function () {
            $scope.deleteIdsSet = {};
            $scope.allChecked = false;

            $scope.resetCriteria();
            $scope.searchActivities($scope.criteria);
        };

        $scope.checkItem = function (item) {
            if (item.checked) {
                $scope.deleteIdsSet[item.activityId] = 1;
            } else {
                delete $scope.deleteIdsSet[item.activityId];
            }
        };

        $scope.selectAll = function (allChecked) {
            for (var index in $scope.activities) {
                $scope.activities[index].checked = allChecked;
                if (allChecked) {
                    $scope.deleteIdsSet[$scope.activities[index].activityId] = 1;
                }
            }
            if (!allChecked) {
                $scope.deleteIdsSet = {};
            }
        };

        $scope.deleteActivities = function () {
            var deleteIds = [];
            for (var item in $scope.deleteIdsSet) {
                deleteIds.push(item);
            }
            if (deleteIds.length == 0) {
                return;
            }
            if ($window.confirm("确定删除")) {

                $rootScope.loadingPromise = activityService.deleteActivities(deleteIds).success(function (result) {
                    if (result.result == 0) {
                        $window.alert("删除成功");
                        $scope.searchActivities($scope.criteria);
                    } else {
                        $window.alert(result.errmsg);
                    }
                });
            }
        };


        /**
         * Initialize component status
         */
        $scope.initData();
    }]);