'use strict';

/**
 * Activity controller
 *
 * @constructor
 */
gpjApp.controller('activityController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'chewanService',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, chewanService, moment) {

        chewanService.getCityList().success(function (result) {
            $scope.cityOptions = (result.result === 0 ? result.data : undefined);
        });

        /**
         * Define data table columns
         */
        $scope.dtColumnDefs = [DTColumnDefBuilder.newColumnDef(0),
            DTColumnDefBuilder.newColumnDef(1),
            DTColumnDefBuilder.newColumnDef(2),
            DTColumnDefBuilder.newColumnDef(3),
            DTColumnDefBuilder.newColumnDef(4),
            DTColumnDefBuilder.newColumnDef(5),
            DTColumnDefBuilder.newColumnDef(6),
            DTColumnDefBuilder.newColumnDef(7),
            DTColumnDefBuilder.newColumnDef(8),
            DTColumnDefBuilder.newColumnDef(9),
            DTColumnDefBuilder.newColumnDef(10),
            DTColumnDefBuilder.newColumnDef(11).notSortable(),
            DTColumnDefBuilder.newColumnDef(12)];

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
            $scope.criteria = {city: '', type: '', key: 'latest'};
        };

        /**
         * Search activities based on criteria
         */
        $scope.searchActivities = function (criteria) {
            //alert(JSON.stringify(criteria));

            $rootScope.loadingPromise = chewanService.getActivityList(criteria).success(function (result) {
                $scope.activities = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * View activity info
         */
        $scope.viewActivity = function (activityId) {
            chewanService.setActivity(activityId);
            var modalInstance = $modal.open({
                templateUrl: 'views/chewan/activity_info_modal.html',
                controller: 'activityInfoModalController'
            });

            return modalInstance.result.then(function (reply) {
                $scope.searchActivities($scope.criteria);
            });
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();
    }]);