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
        }


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
            }).withOption('order', [0, 'desc']);

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
            //document.getElementById("startDate").value = "";
            //document.getElementById("endDate").value = "";
        };

        /**
         * Search users based on criteria
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

        $scope.sendOnFlag = function (officialActivityId) {
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

        $scope.viewOfficialActivity = function (officialActivityId) {
            //officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/view/"+officialActivityId);
        };

        $scope.updateOfficialActivity = function (officialActivityId) {
            $location.path(("/officialActivity/update/"+officialActivityId));
        };

        $scope.checkOnItemStatus = function (onFlag, end) {
            if (onFlag == false) {
                //未上架
                return 0;
            } else {
                //上架中
                var endNum = parseFloat(end);
                if (isNaN(endNum)) {
                    return 1;
                } else {
                    var nowTime = new Date().getTime();
                    //当前时间大于 活动 截止时间 活动处于下架状态
                    if (nowTime > endNum) {
                        return 2;
                    } else {
                        //活动没有到截止时间 处于 上架中
                        return 1;
                    }
                }
            }
        };

        $scope.checkItem = function (item) {
            //var id = itemCheckbox.id.substring(9,itemCheckbox.id.length);
            //if(itemCheckbox.checked){
            //    //如果被选中
            //    //已经存在
            //    for(var index in $scope.deleteIds) {
            //        if($scope.deleteIds[index] === id) {
            //            return;
            //        }
            //    }
            //    $scope.deleteIds.push(id);
            //}else{
            //    //不选中
            //    for(var index in $scope.deleteIds) {
            //        if($scope.deleteIds[index] === id) {
            //           $scope.deleteIds.splice(index,1)
            //        }
            //    }
            //}
            if (item.checked) {
                $scope.deleteIdsSet[item.officialActivityId] = 1;
            } else {
                delete $scope.deleteIdsSet[item.officialActivityId];
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

        $scope.selectAll = function (allChecked) {
            if (allChecked) {
                for (var index in $scope.officialActivities) {
                    var item = $scope.officialActivities[index];
                    //不在上架状态中
                    if ($scope.checkOnItemStatus(item.onFlag, item.end) != 1) {
                        if (allChecked) {
                            $scope.deleteIdsSet[item.officialActivityId] = 1;
                            item.checked = allChecked;
                        }
                    }
                }
            }
        };

        $scope.selectAll = function (allChecked) {
            if (allChecked) {
                for (var index in $scope.officialActivities) {
                    var item = $scope.officialActivities[index];
                    //不在上架状态中
                    if ($scope.checkOnItemStatus(item.onFlag, item.end) != 1) {
                        if (allChecked) {
                            $scope.deleteIdsSet[item.officialActivityId] = 1;
                            item.checked = allChecked;
                        }
                    }
                }
            }
        };


        $scope.resetCriteria();

        $scope.deleteIdsSet = {};

        $scope.searchOfficialActivities($scope.criteria);
    }]);