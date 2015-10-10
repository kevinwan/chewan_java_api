'use strict';

/**
 * Driver/Photo/IDCard authentication controller
 *
 * @constructor
 */
gpjApp.controller('driverAuthenticateController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'chewanService',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, chewanService, moment) {

        var STATUS_PENDING = '认证中';
        var STATUS_ACCEPTED = '认证通过';
        var STATUS_DECLINED = '认证未通过';

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
         *
         * Initial criteria
         */
        $scope.criteria = {
            status: '',
            startDate: (new Date()).setHours(0, 0, 0, 0),
            endDate: (new Date()).setHours(23, 59, 59),
            type: '车主认证'
        };

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            var startTime = new Date();
            startTime.setHours(0, 0, 0, 0);

            var endTime = new Date();
            endTime.setHours(23, 59, 59);

            $scope.criteria = {status: '', startDate: startTime, endDate: endTime, type: '车主认证'};
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