'use strict';

var gpjApp = angular.module('gpjApp', ['ngRoute', 'ngResource', 'ngSanitize', 'datatables', 'angularMoment',
    'ui.bootstrap', 'cgBusy', 'ngAnimate', 'angular-md5', 'angularCharts', 'notifications',
    'ui.date', 'datatables.bootstrap', 'mgcrea.ngStrap.timepicker']);

//var API_BASE = '/api';
//var CHEWAN_API_HOST = '';
//var CHEWAN_API_PORT = '';
//
//var GPJ_API_BASE = '/mobile/oss';
//
//var GPJ_API_HOST = 'http://api7.gongpingjia.com';
//var GPJ_API_PORT = '80';

//var GPJ_API_HOST = 'http://api7.eyelee.cn';
//var GPJ_API_PORT = '80';

//var GPJ_API_HOST = 'http://58.240.32.162';
//var GPJ_API_PORT = '45678';

//var GPJ_API_HOST = 'http://192.168.1.81';
//var GPJ_API_PORT = '8001';

//gpjApp.constant('AuthApiPrefix', API_BASE + '/auth');
//gpjApp.constant('ChewanApiProvider', CHEWAN_API_HOST ? (CHEWAN_API_HOST + ':' + CHEWAN_API_PORT) : '');
//gpjApp.constant('ChewanApiEndPoint', API_BASE + '/chewan');
//
//gpjApp.constant('GpjApiProvider', GPJ_API_HOST ? (GPJ_API_HOST + ':' + GPJ_API_PORT) : '');
//gpjApp.constant('GpjApiEndPoint', GPJ_API_BASE);
//
//var ERROR_MSG_NOLOGIN = '不是职员';

gpjApp.constant('ChewanOfficialApiEndPoint', 'http://192.168.1.145:8080');
gpjApp.constant("ChewanApiProvider", "http://192.168.1.145:8080");
gpjApp.constant("ChewanApiEndPoint", "/v2");

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
}]).config(function ($timepickerProvider) {
    angular.extend($timepickerProvider.defaults, {
        timeFormat: 'HH:mm',
        length: 7
    });
});

///**
// * Router configuration
// */
//gpjApp.config(['$routeProvider', function ($routeProvider) {
//    $routeProvider.when('/', {
//        templateUrl: 'views/gpj/dealer_publish.html',
//        controller: 'dealerPublishController'
//    }).when('/cars/nodealer', {
//        templateUrl: 'views/gpj/nodealer_want_cars.html',
//        controller: 'noDealerWantCarsController'
//    }).when('/cars/dealer', {
//        templateUrl: 'views/gpj/dealer_want_cars.html',
//        controller: 'dealerWantCarsController'
//    }).when('/dealer/list', {
//        templateUrl: 'views/gpj/dealers.html',
//        controller: 'dealerController'
//    }).when('/client/publish', {
//        templateUrl: 'views/gpj/client_publish.html',
//        controller: 'clientPublishCarsController'
//    }).when('/dealer/publish', {
//        templateUrl: 'views/gpj/dealer_publish.html',
//        controller: 'dealerPublishController'
//    }).when('/car/exchange', {
//        templateUrl: 'views/gpj/car_exchange.html',
//        controller: 'carExchangeController'
//    }).when('/c2c/exchange', {
//        templateUrl: 'views/gpj/c2c_exchange.html',
//        controller: 'c2cExchangeController'
//    }).when('/user/appointment', {
//        templateUrl: 'views/gpj/user_appointment.html',
//        controller: 'userAppointmentController'
//    }).when('/scan/list', {
//        templateUrl: 'views/gpj/promo_scan_list.html',
//        controller: 'promoScanController'
//    }).when('/scan/rank', {
//        templateUrl: 'views/gpj/promo_scan_rank.html',
//        controller: 'promoRankController'
//    });
//}]).config(['$httpProvider', function ($httpProvider) {
//    $httpProvider.defaults.useXDomain = true;
//    $httpProvider.defaults.withCredentials = true;
//    delete $httpProvider.defaults.headers.common["X-Requested-With"];
//    $httpProvider.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';
//
//    /**
//     * Combine query parameters in url
//     *
//     * @param data
//     * @returns {*}
//     */
//    $httpProvider.defaults.transformRequest = function (data) {
//        return (data === undefined) ? data : $.param(data);
//    };
//
//    /**
//     * Common error handler
//     */
//    $httpProvider.responseInterceptors.push(['$q', '$window', 'authService', function ($q, $window, authService) {
//        return function (promise) {
//            return promise.then(function (response) {
//                //console.log('success in interceptor');
//                //console.dir(response);
//                if (response.data && response.data.msg === ERROR_MSG_NOLOGIN) {
//                    alert('会话失效');
//                    authService.setUser(undefined);
//                    $window.location.href = '/login.html';
//                    return $q.reject(response);
//                }
//                return response;
//
//            }, function (response) {
//                //console.log('error in interceptor');
//                //console.dir(response);
//                alert('网络访问错误');
//                return $q.reject(response);
//            });
//        };
//    }]);
//}]);

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
            $window.location.href = '/login.html';
        }
    });
}]);

