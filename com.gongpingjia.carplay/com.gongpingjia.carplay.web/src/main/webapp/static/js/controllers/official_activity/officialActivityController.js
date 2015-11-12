'use strict';

/**
 * official activity controller
 *
 * @constructor
 */
gpjApp.controller('officialActivityController', ['$scope', '$rootScope', '$location', '$window', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'officialActivityService',
    'moment', function ($scope, $rootScope, $location, $window, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, officialActivityService, moment) {


        $scope.createActivity = function () {
            officialActivityService.setOfficialActivityId("");
            $location.path("/officialActivity/add");
        };

        $scope.viewOfficialActivity = function (officialActivityId) {
            //officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/view/" + officialActivityId);
        };

        $scope.updateOfficialActivity = function (officialActivityId) {
            $location.path(("/officialActivity/update/" + officialActivityId));
        };

        $scope.updateOfficialActivityLimit = function (officialActivityId) {
            $location.path("/officialActivity/updateLimit/" + officialActivityId);
        };


        var rowClickHandler = function (info) {
        };

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [
            DTColumnDefBuilder.newColumnDef(0).notSortable(),
            DTColumnDefBuilder.newColumnDef(1).notSortable(),
            DTColumnDefBuilder.newColumnDef(2),
            DTColumnDefBuilder.newColumnDef(3),
            DTColumnDefBuilder.newColumnDef(4),
            DTColumnDefBuilder.newColumnDef(5),
            DTColumnDefBuilder.newColumnDef(6),
            DTColumnDefBuilder.newColumnDef(7),
            DTColumnDefBuilder.newColumnDef(8),
            DTColumnDefBuilder.newColumnDef(9).notSortable().notVisible()
        ];

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
            }).withOption('order', [0, 'desc'])
            .withOption('displayLength', 100);

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            $scope.criteria = {
                title: '',
                detailAddress: '',
                status: '-1',
                fromDate: moment().subtract(1, 'month').format('YYYY-MM-DD'),
                toDate: moment().format('YYYY-MM-DD')
            };
        };

        /**
         * search official activity;
         * @param criteria criteria = $scope.criteria
         */
        $scope.searchOfficialActivities = function (criteria) {

            var startDate = $scope.criteria.fromDate; //document.getElementById("startDate").value;
            var endDate = $scope.criteria.toDate; //document.getElementById("endDate").value;
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
            $rootScope.loadingPromise = officialActivityService.getOfficialActivityList(criteria).success(function (result) {
                $scope.officialActivities = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * 上架
         * @param officialActivityId
         */
        $scope.sendOnFlag = function (officialActivityId) {
            if (!confirm("上架以后活动信息将无法修改!请确定是否上架?")) {
                return;
            }
            $rootScope.loadingPromise = officialActivityService.sendOnFlag(officialActivityId).success(function (result) {
                if (result.result === 0) {
                    for (var index in $scope.officialActivities) {
                        if ($scope.officialActivities[index].officialActivityId === officialActivityId) {
                            $scope.officialActivities[index].onFlag = false;
                            $window.alert("上架成功");
                            break;
                        }
                    }
                } else {
                    $window.alert(result.errmsg);
                }
                $scope.searchOfficialActivities($scope.criteria);
            });
        };


        $scope.checkOnItemStatus = function (onFlag, start) {
            if (onFlag == false) {
                //未上架
                return 0;
            } else {
                //上架中
                var startNum = parseFloat(start);
                if (isNaN(startNum)) {
                    return 1;
                } else {
                    var nowTime = new Date().getTime();
                    //当前时间大于 活动 开始时间 活动处于下架状态
                    if (nowTime > startNum) {
                        return 2;
                    } else {
                        //活动没有到截止时间 处于 上架中
                        return 1;
                    }
                }
            }
        };

        $scope.checkItem = function (item) {
            if (item.checked) {
                $scope.deleteIdsSet[item.officialActivityId] = 1;
            } else {
                delete $scope.deleteIdsSet[item.officialActivityId];
            }
        };


        $scope.selectAll = function (allChecked) {
            if (allChecked) {
                for (var index in $scope.officialActivities) {
                    var item = $scope.officialActivities[index];
                    //不在上架状态中
                    if ($scope.checkOnItemStatus(item.onFlag, item.start) != 1) {
                        $scope.deleteIdsSet[item.officialActivityId] = 1;
                        item.checked = allChecked;
                    }
                }
            } else {
                //全部不选中
                for (var index in $scope.officialActivities) {
                    var item = $scope.officialActivities[index];
                    //不在上架状态中
                    if ($scope.checkOnItemStatus(item.onFlag, item.start) != 1) {
                        item.checked = allChecked;
                    }
                }
                $scope.deleteIdsSet = {};
            }
        };


        $scope.deleteOfficialActivities = function () {
            if ($window.confirm("确定删除")) {
                var deleteIds = [];
                for (var item in $scope.deleteIdsSet) {
                    deleteIds.push(item);
                }
                $rootScope.loadingPromise = officialActivityService.deleteOfficialActivities(deleteIds).success(function (result) {
                    if (result.result == 0) {
                        $window.alert("删除成功");
                        $scope.searchOfficialActivities($scope.criteria);
                    } else {
                        $window.alert(result.errmsg);
                    }
                });
            }
        };


        $scope.resetCriteria();

        $scope.deleteIdsSet = {};

        $scope.searchOfficialActivities($scope.criteria);
    }]);