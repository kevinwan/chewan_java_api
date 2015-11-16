'use strict';

/**
 * User controller
 *
 * @constructor
 */
gpjApp.controller('userController', ['$scope', '$rootScope', '$http', '$location', 'DTOptionsBuilder', 'DTColumnDefBuilder', 'userService',
    function ($scope, $rootScope, $http, $location, DTOptionsBuilder, DTColumnDefBuilder, userService) {


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
            DTColumnDefBuilder.newColumnDef(10)];

        /**
         * Data table row click handler
         */
        var rowClickHandler = function (info) {
        };

        /**
         * Define data table options
         */
        $scope.dtOptions = DTOptionsBuilder.newOptions().withOption('displayLength', 100)
            .withOption('order', [0, 'desc']);

        /**
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            var start = new Date();
            start.setHours(0, 0, 0, 0);
            start.setTime(start.getTime() - 1000 * 60 * 60 * 24 * 7);

            var end = new Date();
            end.setHours(0, 0, 0, 0);

            $scope.criteria = {
                phone: '',
                nickname: '',
                licenseAuthStatus: '',
                photoAuthStatus: '',
                startDate: moment().subtract(1, 'month').format('YYYY-MM-DD'),
                endDate: moment().format('YYYY-MM-DD')
            };
        };

        /**
         * Search users based on criteria
         */
        $scope.searchUsers = function () {

            $scope.criteria.start = new Date($scope.criteria.startDate).getTime();
            $scope.criteria.end = new Date($scope.criteria.endDate).getTime();

            $rootScope.loadingPromise = userService.listUsers($scope.criteria).success(function (result) {
                $scope.users = (result.result === 0 ? result.data : undefined);
            });
        };

        /**
         * View user info
         */
        $scope.viewUser = function (userId) {
            userService.setUser(userId);
            $location.path("/user/detail/" + userId);
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();

        /**
         * 进入页面执行一次查询操作
         */
        $scope.searchUsers(this.criteria);
    }]);