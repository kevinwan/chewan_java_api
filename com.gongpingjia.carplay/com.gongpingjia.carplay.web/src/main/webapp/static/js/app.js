'use strict';

//var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
//    'ui.bootstrap', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
//    'ui.date', 'datatables.bootstrap', 'mgcrea.ngStrap.timepicker']);

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date', 'datatables.bootstrap','timepickerPop']);


gpjApp.constant('ChewanOfficialApiEndPoint', 'http://cwapi.gongpingjia.com:8080/v2');
gpjApp.constant("ChewanApiProvider", "http://cwapi.gongpingjia.com:8080");
gpjApp.constant("ChewanApiEndPoint", "/v2");

//gpjApp.constant('ChewanOfficialApiEndPoint', 'http://localhost:8080');
//gpjApp.constant("ChewanApiProvider", "http://localhost:8080");
//gpjApp.constant("ChewanApiEndPoint", "");

/**
 * Router configuration
 */
gpjApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/chewan/official_activity/list.html',
        controller: 'officialActivityController'
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
        templateUrl: 'views/user/user.html',
        controller: 'userController'
    }).when('/user/detail/:id', {
        templateUrl: 'views/user/user_detail.html',
        controller: 'userDetailController'
    }).when('/activity/list', {
        templateUrl: 'views/activity/list.html',
        controller: 'activityController'
    }).when('/activity/view', {
        templateUrl: 'views/activity/view.html',
        controller: 'activityViewController'
    }).when('/activity/update/:id', {
        templateUrl: 'views/activity/update.html',
        controller: 'activityUpdateController'
    }).when('/officialActivity/list', {
        templateUrl: 'views/chewan/official_activity/list.html',
        controller: 'officialActivityController'
    }).when('/officialActivity/add', {
        templateUrl: 'views/chewan/official_activity/add.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/update/:id', {
        templateUrl: 'views/chewan/official_activity/update.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/view/:id', {
        templateUrl: 'views/chewan/official_activity/view.html',
        controller: 'officialActivityEditController'
    });
}]);
//    .config(function ($timepickerProvider) {
//    angular.extend($timepickerProvider.defaults, {
//        timeFormat: 'HH:mm',
//        length: 7
//    });
//});

gpjApp.value('cgBusyDefaults', {
    message: '页面加载中，请稍候...',
    backdrop: true,
    templateUrl: 'views/common/loading-template.html',
    delay: 0,
    minDuration: 0,
    wrapperClass: 'my-class my-class2'
});


gpjApp.run(['$rootScope', '$location', '$window', 'authService', function ($rootScope, $location, $window, authService) {

    /**
     * Process route change start
     */
    $rootScope.$on('$routeChangeStart', function (event, next, current) {
        var loginUser = authService.getUser();
        if (!loginUser) {
            event.preventDefault();
            $window.location.href = 'login.html';
        }
    });
}]);

