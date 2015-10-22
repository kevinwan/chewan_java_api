'use strict';

/**
 * userService
 *
 * @constructor
 */

gpjApp.factory('userService', ['authService', 'restProxyService', 'ChewanOfficialApiEndPoint', 'commonService',
    function (authService, restProxyService, ChewanOfficialApiEndPoint, commonService) {

        var userId;

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

            setUser: function (aUserId) {
                userId = aUserId;
            },

            listUsers: function (criteria) {
                var param = "";
                param += "&phone=" + criteria.phone;
                param += "&nickname=" + criteria.nickname;
                param += "&licenseAuthStatus=" + criteria.licenseAuthStatus;
                param += "&photoAuthStatus=" + criteria.photoAuthStatus;
                param += "&start=" + criteria.start;
                param += "&end=" + criteria.end;

                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/list?userId="
                + authService.getUser().userId + "&token=" + authService.getUser().token + param);
            },

            getUserInfo: function (userId) {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/" + userId + "/detail?userId="
                + authService.getUser().userId + "&token=" + authService.getUser().token);
            },

            updateUserInfo: function (user) {
                var photoIds = new Array();
                for (var index in user.album) {
                    photoIds.push(user.album[index].id);
                }

                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, "/user/" + user.userId + "/update?userId="
                    + authService.getUser().userId + "&token=" + authService.getUser().token,
                    JSON.stringify({
                        role: user.role,
                        deleteFlag: user.deleteFlag,
                        photoIds: photoIds
                    }));
            }
        }
    }]);