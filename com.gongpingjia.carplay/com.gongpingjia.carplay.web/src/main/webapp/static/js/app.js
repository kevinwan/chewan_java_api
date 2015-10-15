'use strict';

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap','mgcrea.ngStrap.datepicker', 'mgcrea.ngStrap.timepicker', 'mgcrea.ngStrap.tab', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date']);

gpjApp.constant('ChewanOfficialApiEndPoint', 'http://127.0.0.1:8000');
gpjApp.constant("ChewanApiProvider", "http://127.0.0.1:8080");
gpjApp.constant("ChewanApiEndPoint", "");


/**
 * Router configuration
 */
gpjApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/chewan/user.html',
        controller: 'userController'
    }).when('/driverAuthentication/list', {
        templateUrl: 'views/auth/driver_authentication.html',
        controller: 'driverAuthenticateController'
    }).when('/driverAuthentication/detail', {
        templateUrl: 'views/auth/driver_authentication_detail.html',
        controller: 'driverAuthenticateDetailController'
    }).when('/photoAuthentication/list', {
        templateUrl: 'views/auth/user_photo_authentication.html',
        controller: 'userPhotoAuthenticationController'
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
        templateUrl: 'views/chewan/official_activity/list.html',
        controller: 'officialActivityController'
    }).when('/officialActivity/add', {
        templateUrl: 'views/chewan/official_activity/add.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/update', {
        templateUrl: 'views/chewan/official_activity/update.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/view', {
        templateUrl: 'views/chewan/official_activity/view.html',
        controller: 'officialActivityEditController'
    });
}]).config(function($datepickerProvider) {
        angular.extend($datepickerProvider.defaults, {
            dateFormat: 'yyyy-MM-dd',
            startWeek: 1
        });

    }).config(function($timepickerProvider) {
    angular.extend($timepickerProvider.defaults, {
        timeFormat: 'HH:mm',
        length: 7
    });
});

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
            $window.location.href = '/static/login.html';
        }
    });
}]);

