'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'ChewanOfficialApiEndPoint',
    function (authService, restProxyService, ChewanOfficialApiEndPoint) {

        return {

            logIn: function (phone, password) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, '/user/login', {
                    phone: phone,
                    password: password
                });
            },

            logOut: function () {
                authService.setUser(undefined);
            }
        }
    }]);