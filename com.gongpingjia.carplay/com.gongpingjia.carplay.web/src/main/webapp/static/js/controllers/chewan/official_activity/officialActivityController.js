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
            $scope.criteria = {title: '', detailAddress: '', onFlag: '-1',status:'-1'};
            document.getElementById("startDate").value = "";
            document.getElementById("endDate").value = "";
        };

        /**
         * Search users based on criteria
         */
        $scope.searchOfficialActivities = function (criteria) {

            var startDate = document.getElementById("startDate").value;
            var endDate = document.getElementById("endDate").value;
            if(startDate !== ""){
                criteria.fromTime = new Date(startDate).getTime();
            }else{
                criteria.fromTime = "";
            }
            if(endDate !== ""){
                criteria.toTime = new Date(startDate).getTime();
            }else{
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
            officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/view");
        };

        $scope.updateOfficialActivity = function (officialActivityId) {
            officialActivityService.setOfficialActivityId(officialActivityId);
            $location.path("/officialActivity/update");
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

        $scope.checkItem = function(itemCheckbox){
            var id = itemCheckbox.id.substring(9,itemCheckbox.id.length);
            if(itemCheckbox.checked){
                //如果被选中
                //已经存在
                for(var index in $scope.deleteIds) {
                    if($scope.deleteIds[index] === id) {
                        return;
                    }
                }
                $scope.deleteIds.push(id);
            }else{
                //不选中
                for(var index in $scope.deleteIds) {
                    if($scope.deleteIds[index] === id) {
                       $scope.deleteIds.splice(index,1)
                    }
                }
            }
        };

        $scope.deleteOfficialActivities = function(){
            if($window.confirm("确定删除")){
                $rootScope.loadingPromise =   officialActivityService.deleteOfficialActivities($scope.deleteIds).success(function(result){
                    if(result.result == 0) {
                        $window.alert("删除成功");
                     $scope.searchOfficialActivities($scope.criteria);
                    }else{
                        $window.alert(result.errmsg);
                    }
                });
            }
        };

        $scope.selectAll = function(){
            var allItem = document.getElementById("checkItemAll");
            if(allItem.checked) {
                //
                var items =   document.getElementsByName("checkItem");
                for(var index in items) {
                    items[index].checked = true;
                }
            }
            var name = '123';
        };


        $scope.resetCriteria();
        $scope.deleteIds = [];
        $scope.searchOfficialActivities($scope.criteria);
    }]);