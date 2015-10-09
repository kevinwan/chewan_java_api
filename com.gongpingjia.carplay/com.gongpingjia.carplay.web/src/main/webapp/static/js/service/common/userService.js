'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'AuthApiPrefix',
    function (authService, restProxyService, AuthApiPrefix) {

        return {
            logIn: function (username, password) {
                return restProxyService.sendHttpPost(AuthApiPrefix, '/user/login', {
                    username: username,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
            }
        }
    }]);