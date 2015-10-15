'use strict';

/**
 * User controller
 *
 * @constructor
 */
gpjApp.controller('userController', ['$scope', '$rootScope', '$http', '$modal', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'chewanService',
    'moment', function ($scope, $rootScope, $http, $modal, DTOptionsBuilder, DTColumnDefBuilder, chewanService, moment) {

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
            DTColumnDefBuilder.newColumnDef(9).notSortable(),
            DTColumnDefBuilder.newColumnDef(10)];

        /**
         * Data table row click handler
         */
        var rowClickHandler = function (info) {
        };

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
            $scope.criteria = {phone: '', nickname: '', isAuthenticated: ''};
        };

        /**
         * Search users based on criteria
         */
        $scope.searchUsers = function (criteria) {
            //alert(JSON.stringify(criteria));

            $rootScope.loadingPromise = chewanService.getUserList(criteria).success(function (result) {
                $scope.users = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * View user info
         */
        $scope.viewUser = function (userId) {
            chewanService.setUser(userId);
            var modalInstance = $modal.open({
                templateUrl: 'views/chewan/user_info_modal.html',
                controller: 'userInfoModalController'
            });

            return modalInstance.result.then(function (reply) {
                $scope.searchUsers($scope.criteria);
            });
        };

        /**
         * Get status color
         */
        //$scope.getStatusColor = function (status) {
        //    if (status === STATUS_DECLINED)
        //        return {'color': 'red', 'font-weight': 'bold'};
        //    else if (status === STATUS_ACCEPTED)
        //        return {'color': 'green', 'font-weight': 'bold'};
        //    else
        //        return {'color': 'brown', 'font-weight': 'bold'};
        //};

        /**
         * Initialize component status
         */
        $scope.resetCriteria();
    }]);