'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'AuthApiPrefix',
    function (authService, restProxyService, AuthApiPrefix) {

        return {
            logIn: function (phone, password) {
                return restProxyService.sendHttpPost(AuthApiPrefix, '/user/login', {
                    phone: phone,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
            }
        }
    }]);