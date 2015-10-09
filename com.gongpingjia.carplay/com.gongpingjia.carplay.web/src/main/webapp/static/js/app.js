'use strict';

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap', 'mgcrea.ngStrap.tab', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date']);

var CHEWAN_API_URL = 'http://127.0.0.1:8080';

gpjApp.constant('AuthApiPrefix', CHEWAN_API_URL);
gpjApp.constant('ChewanApiProvider', CHEWAN_API_URL);
gpjApp.constant('ChewanApiEndPoint', CHEWAN_API_URL);

/**
 * Router configuration
 */
gpjApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/operation/operation_main.html',
        controller: 'userStatisticsController'
    }).when('/authentication/list', {
        templateUrl: 'views/chewan/driver_authentication.html',
        controller: 'driverAuthenticateController'
    }).when('/feedback/list', {
        templateUrl: 'views/chewan/feedback_main.html',
        controller: 'feedbackController'
    }).when('/user/list', {
        templateUrl: 'views/chewan/user.html',
        controller: 'userController'
    }).when('/activity/list', {
        templateUrl: 'views/chewan/activity.html',
        controller: 'activityController'
    }).when('/operation/main', {
        templateUrl: 'views/operation/operation_main.html',
        controller: 'userStatisticsController'
    });
}]);

gpjApp.value('cgBusyDefaults', {
    message: '页面加载中，请稍候...',
    backdrop: true,
    templateUrl: 'views/common/loading-template.html',
    delay: 0,
    minDuration: 0,
    wrapperClass: 'my-class my-class2'
});

gpjApp.run(['$rootScope', '$location', '$window', 'authService', function ($rootScope, $location, $window, authService) {
    $rootScope.$on('$routeChangeStart', function (event) {

        var loginUser = authService.getUser();
        if (!loginUser) {
            event.preventDefault();
            $window.location.href = '/login.html';
        }
    });
}]);

