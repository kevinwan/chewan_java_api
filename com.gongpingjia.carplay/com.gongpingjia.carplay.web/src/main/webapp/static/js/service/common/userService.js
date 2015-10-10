'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'ChewanApiProvider', 'ChewanApiEndPoint',
    function (authService, restProxyService, ChewanApiProvider, ChewanApiEndPoint) {

        return {
            logIn: function (phone, password) {
                return restProxyService.sendHttpPost(ChewanApiProvider + ChewanApiEndPoint, '/user/login', {
                    phone: phone,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
            }
        }
    }]);