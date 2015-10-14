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
            }

            $rootScope.loadingPromise = officialActivityService.getOfficialActivityList(criteria).success(function (result) {
                $scope.officialActivities = (result.result === 0 ? result.data : undefined);
            });
        };

        $scope.sendOnFlag = function (officialActivityId) {
            $rootScope.loadingPromise = officialActivityService.sendOnFlag(officialActivityId).success(function (result) {
                if(result.result === 0) {
                    for (var index in $scope.officialActivities) {
                        if ($scope.officialActivities[index].officialActivityId === index) {
                            $scope.officialActivities[index].onFlag = false;
                            break;
                            $window.alert("上架成功");
                        }
                    }
                }else{
                    $window.alert(result.message);
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

        $scope.resetCriteria();
    }]);