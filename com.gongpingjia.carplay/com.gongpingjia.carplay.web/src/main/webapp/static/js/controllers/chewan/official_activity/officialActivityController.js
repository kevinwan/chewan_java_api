'use strict';

/**
 * official activity controller
 *
 * @constructor
 */
gpjApp.controller('officialActivityController', ['$scope', '$rootScope', '$location','$window', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'officialActivityService',
    'moment', function ($scope, $rootScope, $location,$window, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, officialActivityService, moment) {


        $scope.createActivity = function () {
            officialActivityService.setOfficialActivityId("");
            $location.path("/officialActivity/add");
        }

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
        $scope.dtOptions = DTOptionsBuilder.newOptions().withOption('rowCallback',
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
            //var startTime = new Date();
            //startTime.setHours(0, 0, 0, 0);
            //startTime.setTime(startTime.getTime() - 1000 * 60 * 60 * 24 * 7);
            //
            //var endTime = new Date();
            //endTime.setHours(23, 59, 59);

            $scope.criteria = {title: '', detailAddress: '', startTime: '', onFlag: ''};
        };

        /**
         * Search users based on criteria
         */
        $scope.searchOfficialActivities = function (criteria) {
            if (criteria.startTime != '') {
                //TODO
            }else{
                criteria.startTime == new Date(0);

            }

            $rootScope.loadingPromise = officialActivityService.getOfficialActivityList(criteria).success(function (result) {
                $scope.officialActivities = (result.result === 0 ? result.data : undefined);
            });
        };

        $scope.sendOnFlag = function (officialActivityId) {
            $rootScope.loadingPromise = officialActivityService.sendOnFlag(officialActivityId).success(function (result) {
                if(result.result === 0) {
                    for (var index in $scope.officialActivities) {
                        if ($scope.officialActivities[index].officialActivityId === officialActivityId) {
                            $scope.officialActivities[index].onFlag = false;
                            $window.alert("上架成功");
                            break;
                        }
                    }
                    $scope.$apply();
                }else{
                    $window.alert(result.errmsg);
                }

            });
        };

        $scope.viewOfficialActivity = function(officialActivityId) {
            officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/view");
        };

        $scope.updateOfficialActivity = function(officialActivityId) {
            officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/update");
        };

        $scope.checkOnItemStatus = function(onFlag,end){
            if(onFlag == false) {
                //未上架
                return 0;
            }else{
                //上架中
                if(end == undefined || end == null || end == "") {
                    return 1;
                }else{
                    var nowTime = new Date().getTime();
                    //当前时间大于 活动 截止时间 活动处于下架状态
                    if(nowTime > end) {
                        return 2;
                    }else{
                        //活动没有到截止时间 处于 上架中
                        return 1;
                    }
                }
            }
        };

        $scope.resetCriteria();
    }]);