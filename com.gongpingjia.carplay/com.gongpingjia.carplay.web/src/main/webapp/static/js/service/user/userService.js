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
                param += "&start=" + commonService.transferDateStringToLong(criteria.start);
                param += "&end=" + commonService.transferDateStringToLong(criteria.end);

                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/list?userId="
                + authService.getUser().userId + "&token=" + authService.getUser().token + param);
            },

            getUserInfo: function () {
                return restProxyService.sendHttpGet(ChewanOfficialApiEndPoint, "/user/" + userId + "/detail?userId="
                + authService.getUser().userId + "&token=" + authService.getUser().token);
            },

            updateUserInfo: function (user) {
                var photoIds = new Array();
                for (var photo in user.album) {
                    photoIds.add(photo.id);
                }

                return restProxyService.sendHttpPost(ChewanOfficialApiEndPoint, "/user/" + user + "/update?userId="
                    + authService.getUser().userId + "&token=" + authService.getUser().token,
                    JSON.stringify({
                        role: user.role,
                        deleteFlag: user.deleteFlag,
                        photoIds: photoIds
                    }));
            }
        }
    }]);