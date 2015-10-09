'use strict';

/**
 * Driver authentication controller
 *
 * @constructor
 */
gpjApp.controller('driverAuthenticateController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'chewanService',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, chewanService, moment) {

        var STATUS_PENDING = '待处理';
        var STATUS_ACCEPTED = '已同意';
        var STATUS_DECLINED = '已拒绝';

        /**
         * If radio selection changes, empty the table contents
         */
        //$scope.$watch('criteria.status', function () {
        //    $scope.applications = [];
        //});

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
            DTColumnDefBuilder.newColumnDef(7).notSortable(),
            DTColumnDefBuilder.newColumnDef(8)];

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
            var today = '';
            $scope.criteria = {status: '', startDate: today, endDate: today};
        };

        /**
         * Search authentication applications based on criteria
         */
        $scope.searchApplications = function (criteria) {
            //alert(JSON.stringify(criteria));

            $rootScope.loadingPromise = chewanService.getApplicationList(criteria).success(function (result) {
                $scope.applications = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * View processed application
         */
        $scope.viewApplication = function (applicationId) {
            chewanService.setApplication(applicationId);
            var modalInstance = $modal.open({
                templateUrl: 'views/chewan/driver_authentication_info_modal.html',
                controller: 'authenticateInfoModalController'
            });

            return modalInstance.result.then(function (reply) {
                $scope.searchApplications($scope.criteria);
            });
        };

        /**
         * Get status color
         */
        $scope.getStatusColor = function (status) {
            if (status === STATUS_DECLINED)
                return {'color': 'red', 'font-weight': 'bold'};
            else if (status === STATUS_ACCEPTED)
                return {'color': 'green', 'font-weight': 'bold'};
            else
                return {'color': 'brown', 'font-weight': 'bold'};
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();
    }]);