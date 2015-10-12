'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'GpjApiProvider', 'GpjApiEndPoint',
    function (authService, restProxyService, GpjApiProvider, GpjApiEndPoint) {

        return {

            logIn: function (username, password) {
                return restProxyService.sendHttpPost(GpjApiProvider, GpjApiEndPoint + '/post-login/', {
                    username: username,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
                return restProxyService.sendHttpGet(GpjApiProvider, GpjApiEndPoint + '/get-oss-logout/');
            }
        }
    }]);