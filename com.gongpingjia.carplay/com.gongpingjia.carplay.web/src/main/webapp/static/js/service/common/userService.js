'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['$window', 'authService', 'restProxyService', 'ChewanOfficialApiEndPoint',
    function ($window, authService, restProxyService, ChewanOfficialApiEndPoint) {

        return {
            logIn: function (phone, password) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/user/login', {
                    phone: phone,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
                $window.location.href = '/v2/login.html';
            }
        }
    }]);