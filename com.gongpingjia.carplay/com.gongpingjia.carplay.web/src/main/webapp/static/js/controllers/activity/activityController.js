'use strict';

/**
 * Activity controller
 *
 * @constructor
 */
gpjApp.controller('activityController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', '$compile', 'DTInstances', 'activityService', 'commonService', '$location', '$window',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, $compile, DTInstances, activityService, commonService, $location, $window, moment) {

        $scope.deleteIdSet = {};
        $scope.allIds = [];
        $scope.allCheckItem = false;

        //选中某一个
        $scope.checkItem = function (activityId) {
            if ($scope["checkItem" + activityId] === true) {
                //选中了
                $scope.deleteIdSet[activityId] = -1;
            } else if (!$scope["checkItem" + activityId]) {
                delete  $scope.deleteIdSet[activityId];
            }
        };

        //全部选中 或者 不选中
        $scope.checkAll = function () {
            for (var index in $scope.allIds) {
                $scope["checkItem" + $scope.allIds[index]] = $scope.allCheckItem;
            }
            if ($scope.allCheckItem) {
                for (var index in $scope.allIds) {
                    $scope.deleteIdSet[$scope.allIds[index]] = -1;
                }
            } else {
                $scope.deleteIdSet = {};
            }
        };

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
            DTColumnDefBuilder.newColumnDef(0).withOption('data', null).renderWith(actionCheckBox).notSortable(),
            DTColumnDefBuilder.newColumnDef(1).withOption('data', null).renderWith(actionUserInfo).notSortable(),
            DTColumnDefBuilder.newColumnDef(2).withOption('data', null).renderWith(actionEstablish),
            DTColumnDefBuilder.newColumnDef(3).withOption('data', null).renderWith(actionDestination),
            DTColumnDefBuilder.newColumnDef(4).withOption('data', 'type'),
            DTColumnDefBuilder.newColumnDef(5).withOption('data', null).renderWith(actionPay),
            DTColumnDefBuilder.newColumnDef(6).withOption('data', null).renderWith(actionTransfer).notSortable(),
            DTColumnDefBuilder.newColumnDef(7).withOption('data', null).renderWith(actionCreateTime),
            DTColumnDefBuilder.newColumnDef(8).withOption('data', null).renderWith(actionDeal).notSortable(),
            DTColumnDefBuilder.newColumnDef(9).withOption('data', 'activityId').notVisible().notSortable()
        ];

        function actionCheckBox(data, type, full, meta) {
            return ' <input ng-model="checkItem' + data.activityId +
                '" type="checkbox" name="checkItem" ng-change="checkItem(\'' + data.activityId +
                '\')"/>';
        }

        function actionUserInfo(data, type, full, meta) {
            return ' <span>' + data.nickname +
                '    (' + data.phone +
                ')</span>';
        }

        function actionEstablish(data, type, full, meta) {
            if(data.establish === undefined || data.establish === null) {
                return "无";
            }
            data.establish.city = commonService.transferIllegalToEmpty(data.establish.city);
            data.establish.district = commonService.transferIllegalToEmpty(data.establish.district);
            data.establish.street = commonService.transferIllegalToEmpty(data.establish.street);
            return '<span>' + data.establish.city + '市/' + data.establish.district + '/' + data.establish.street + '</span>';
        }

        function actionDestination(data, type, full, meta) {
            if(data.destination === undefined || data.destination === null) {
                return "无";
            }
            data.destination.city = commonService.transferIllegalToEmpty(data.destination.city);
            data.destination.district = commonService.transferIllegalToEmpty(data.destination.district);
            data.destination.street = commonService.transferIllegalToEmpty(data.destination.street);

            return '<span>' + data.destination.city +
                '市/' + data.destination.district +
                '/' + data.destination.street +
                '</span>';
        }

        function actionPay(data, type, full, meta){
            if(commonService.isNull(data.pay)){
                return "";
            }else{
                return data.pay;
            }

        }

        function actionTransfer(data, type, full, meta) {
            if (data.transfer || data.transfer === 'true') {
                return '是';
            } else {
                return '否';
            }
        }

        function actionCreateTime(data, type, full, meta) {
            return commonService.transferLongToDateTimeString(data.createTime);
        }

        function actionDeal(data, type, full, meta) {
            return ' <button ng-click="updateActivity(\'' + data.activityId +
                '\')">修改</button>'
        }


        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withBootstrap()
            .withOption('ajax', function (data, callback, settings) {
                var criteria = {};

                criteria = $scope.criteria;

                var startDate = $scope.criteria.fromDate;
                var endDate = $scope.criteria.toDate;
                criteria.draw = data.draw;
                criteria.length = data.length;
                criteria.start = data.start;

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

                criteria.province =   commonService.transferIllegalToEmpty(criteria.province);
                criteria.city =   commonService.transferIllegalToEmpty(criteria.province);

                $rootScope.loadingPromise = activityService.getActivityList(criteria).success(function (res) {
                    if (res.result === 0) {
                        //
                        //删除之前的 $scope.checkItem{{activityId}}

                        for (var index in $scope.allIds) {
                            delete  $scope["checkItem" + $scope.allIds[index]];
                        }
                        $scope.deleteIdSet = {};
                        $scope.allIds = [];
                        $scope.allCheckItem = false;


                        $scope.total = res.data.recordsFiltered;
                        res.backInfo = res.data;
                        res.data = res.data.activityList;
                        res.draw = res.backInfo.draw;
                        res.recordsFiltered = res.backInfo.recordsFiltered;
                        res.recordsTotal = res.backInfo.recordsTotal;
                        //$scope.items = res.data;

                        for (var index in res.data) {
                            $scope.allIds.push(res.data[index].activityId);
                        }

                        if (res.data === undefined || res.data === null || res.data.length === 0) {
                            return;
                        }
                        callback(res);
                    }
                });
            })
            .withOption('bFilter', false)
            .withDataProp('data')
            .withOption('displayLength', 100)
            .withOption('serverSide', true)
            .withPaginationType('full_numbers')
            .withOption('createdRow', createdRow)
            .withOption('order', []);

        /**
         * Perform while creating row
         *
         * @param row
         * @param data
         * @param dataIndex
         */
        function createdRow(row, data, dataIndex) {
            // Recompiling so we can bind Angular directive to the DT
            $compile(angular.element(row).contents())($scope);
        };


        $scope.addActivity = function(){
            $location.path("/activity/add");
        };


        $scope.updateActivity = function (activityId) {
            $location.path("/activity/update/" + activityId);
        };


        //删除活动
        $scope.deleteActivities = function () {
            var deleteIds = [];
            for (var item in $scope.deleteIdSet) {
                deleteIds.push(item);
            }
            if (deleteIds.length == 0) {
                return;
            }
            if (confirm("确定删除")) {
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
                fromDate: moment().subtract(1, 'month').format('YYYY-MM-DD'),
                toDate: moment().format('YYYY-MM-DD'),
                fromTime: '',
                toTime: ''
            };
        };


        /**
         * Search requirements
         *
         * @param criteria
         */
        $scope.searchActivities = function (criteria) {
            $scope.dtInstance.rerender();
        };


        $scope.resetCriteria();


        DTInstances.getLast().then(function (dtInstance) {
            $scope.dtInstance = dtInstance;
        });
    }]);