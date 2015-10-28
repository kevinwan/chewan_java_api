/**
 * Created by 123 on 2015/10/13.
 */
'use strict';

/**
 * Driver/Photo/IDCard authentication controller
 *
 * @constructor
 */
gpjApp.controller('userPhotoAuthenticationController', ['$scope', '$rootScope', '$window', 'DTColumnDefBuilder', 'authenticationService',
    function ($scope, $rootScope, $window, DTColumnDefBuilder, authenticationService) {

        var STATUS_PENDING = '认证中';
        var STATUS_ACCEPTED = '认证通过';
        var STATUS_DECLINED = '认证未通过';

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
         * Reset search criteria
         */
        $scope.resetCriteria = function () {
            //var startTime = new Date();
            //startTime.setHours(0, 0, 0, 0);
            //startTime.setTime(startTime.getTime() - 1000 * 60 * 60 * 24 * 7);
            //
            //var endTime = new Date();
            //endTime.setHours(23, 59, 59);

            $scope.criteria = {
                status: '',
                startDate: moment().subtract(1, 'month').format('YYYY-MM-DD'),
                endDate: moment().add('days', 1).subtract('month').format('YYYY-MM-DD'),
                type: '头像认证'
            };
        };

        /**
         * Search authentication applications based on criteria
         */
        $scope.searchApplications = function (criteria) {
            $rootScope.loadingPromise = authenticationService.getApplicationList(criteria).success(function (result) {
                $scope.applications = (result.result === 0 ? result.data : undefined);
            });
        };


        $scope.accept = function (application) {
            authenticationService.processUserPhotoApplication(true, application.remarks, application.applicationId).success(
                function (result) {
                    if (result && result.result == 0) {
                        alert('成功完成头像认证');
                    } else {
                        alert(result.errmsg);
                    }
                    $scope.searchApplications($scope.criteria);
                }
            );
        }

        $scope.decline = function (application) {
            var remarks = application.remarks;
            if (!remarks) {
                alert('请在备注中填写拒绝理由');
                return;
            }
            authenticationService.processUserPhotoApplication(true, application.remarks, application.applicationId).success(
                function (result) {
                    if (result && result.result == 0) {
                        alert('成功完成头像认证');
                    } else {
                        alert(result.errmsg);
                    }
                    $scope.searchApplications($scope.criteria);
                }
            );
        }

        $scope.browsePhoto = function (photoUrl) {
            $window.open(photoUrl, '_blank');
        }

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
         * Reload data on load
         */
        $rootScope.loadingPromise = function () {
            return authenticationService.getApplicationList($scope.criteria).success(function (result) {
                $scope.applications = (result.result === 0 ? result.data : undefined);
            })
        };

        /**
         * Initialize component status
         */
        $scope.resetCriteria();

        /**
         * load data
         */
        $rootScope.loadingPromise();
    }])