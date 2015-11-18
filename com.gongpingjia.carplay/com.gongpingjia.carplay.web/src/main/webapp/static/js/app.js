'use strict';

//var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
//    'ui.bootstrap', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
//    'ui.date', 'datatables.bootstrap', 'mgcrea.ngStrap.timepicker']);

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date', 'datatables.bootstrap', 'timepickerPop']);


gpjApp.constant('ChewanOfficialApiEndPoint', 'http://cwapi.gongpingjia.com:8080/v2');
gpjApp.constant('ChewanApiProvider', 'http://cwapi.gongpingjia.com:8080');
gpjApp.constant('ChewanApiEndPoint', '/v2');

//gpjApp.constant('ChewanOfficialApiEndPoint', 'http://localhost:8080');
//gpjApp.constant("ChewanApiProvider", "http://localhost:8080");
//gpjApp.constant("ChewanApiEndPoint", "");

/**
 * Router configuration
 */
gpjApp.config(['$routeProvider', function ($routeProvider) {
    $routeProvider.when('/', {
        templateUrl: 'views/official_activity/list.html',
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
    }).when('/albumAuthentication/list', {
        templateUrl: 'views/auth/user_album_authentication.html',
        controller: 'userAlbumAuthenticationController'
    }).when('/auth/history/detail', {
        templateUrl: 'views/auth/history_detail.html',
        controller: 'authHistoryDetailController'
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
    }).when('/activity/add', {
        templateUrl: 'views/activity/add.html',
        controller: 'activityAddController'
    }).when('/activity/update/:id', {
        templateUrl: 'views/activity/update.html',
        controller: 'activityUpdateController'
    }).when('/officialActivity/list', {
        templateUrl: 'views/official_activity/list.html',
        controller: 'officialActivityController'
    }).when('/officialActivity/add', {
        templateUrl: 'views/official_activity/add.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/update/:id', {
        templateUrl: 'views/official_activity/update.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/view/:id', {
        templateUrl: 'views/official_activity/view.html',
        controller: 'officialActivityEditController'
    }).when('/officialActivity/updateLimit/:id', {
        templateUrl: 'views/official_activity/update_limit.html',
        controller: 'officialActivityEditController'
    }).when('/statistic/test', {
        templateUrl: 'views/statistic/test.html',
        controller: 'testController'
    }).when('/statistic/common/:type', {
        templateUrl: 'views/statistic/statistic_common.html',
        controller: 'statisticCommonController'
    }).when('/statistic/register', {
        templateUrl: 'views/statistic/statistic_register.html',
        controller: 'statisticRegisterController'
    });
}]).config(['$httpProvider', function ($httpProvider) {

    /**
     * Combine query parameters in url
     *
     * @param data
     * @returns {*}
     */
    //$httpProvider.defaults.transformRequest = function (data) {
    //    return (data === undefined) ? data : $.param(data);
    //};

    /**
     * Common error handler
     */
    $httpProvider.responseInterceptors.push(['$q', '$window', function ($q, $window) {
        return function (promise) {
            return promise.then(function (response) {
                if (response.data && response.data.errmsg && response.data.errmsg.indexOf('请重新登录') >= 0) {
                    alert(response.data.errmsg);
                    //authService.setUser(undefined);
                    $window.location.href = '/v2/login.html';
                    return $q.reject(response);
                }
                return response;

            }, function (response) {
                if (response.data.errmsg)
                    alert('网络访问错误');
                return $q.reject(response);
            });
        };
    }]);
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

