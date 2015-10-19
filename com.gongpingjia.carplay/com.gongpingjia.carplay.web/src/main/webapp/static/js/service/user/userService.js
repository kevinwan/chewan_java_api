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
            },

            listUsers: function (criteria) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/list?userId="
                    + authService.getUser().userId + "&token=" + authService.getUser().token,
                    JSON.stringify({
                        phone: criteria.phone,
                        nickname: criteria.nickname,
                        licenseAuthStatus: criteria.licenseAuthStatus,
                        photoAuthStatus: criteria.photoAuthStatus,
                        start: criteria.start,
                        end: criteria.end
                    }));
            },

            getUserInfo: function () {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/" + user + "/detail?userId="
                + authService.getUser().userId + "&token=" + authService.getUser().token);
            },

            updateUserInfo: function (user) {
                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, "/user/" + user + "/update?userId="
                    + authService.getUser().userId + "&token=" + authService.getUser().token,
                    JSON.stringify({
                        role: user.role,
                        deleteFlag: user.deleteFlag,
                        photos: user.photos
                    }));
            }
        }
    }]);