'use strict';

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap', 'mgcrea.ngStrap.tab', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date']);

gpjApp.constant('ChewanApiProvider', 'http://127.0.0.1:8080');
gpjApp.constant('ChewanApiEndPoint', '/');

/**
 * Router configuration
 */
gpjApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/chewan/officialActivity_main.html',
        controller: 'officialActivityController'
    }).when('/driverAuthentication/list', {
        templateUrl: 'views/chewan/driver_authentication.html',
        controller: 'driverAuthenticateController'
    }).when('/photoAuthentication/list', {
        templateUrl: 'views/chewan/photo_authentication.html',
        controller: 'photoAuthenticationController'
    }).when('/feedback/list', {
        templateUrl: 'views/chewan/feedback_main.html',
        controller: 'feedbackController'
    }).when('/user/list', {
        templateUrl: 'views/chewan/user.html',
        controller: 'userController'
    }).when('/activity/list', {
        templateUrl: 'views/chewan/activity.html',
        controller: 'activityController'
    }).when('/officialActivity/list', {
        templateUrl: 'views/chewan/officialActivity_main.html',
        controller: 'officialActivityController'
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

