/**
 * Created by 123 on 2015/10/13.
 */
'use strict';

/**
 * Driver/Photo/IDCard authentication controller
 *
 * @constructor
 */
gpjApp.controller('authHistoryDetailController', ['$scope', '$rootScope', '$window', 'authenticationService',
    function ($scope, $rootScope, $window, authenticationService) {

        var authHistorys;

        $rootScope.loadingPromise = function () {
            authHistorys = authenticationService.getAuthHistorys();
        };

        $scope.loadingPromise();
    }]);